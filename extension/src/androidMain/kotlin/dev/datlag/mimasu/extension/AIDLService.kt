package dev.datlag.mimasu.extension

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.IInterface
import android.provider.Settings
import dev.datlag.mimasu.extension.model.AppInfo
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.scopeCatching
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import net.pearx.kasechange.toTitleCase
import java.lang.ref.WeakReference

abstract class AIDLService<T : IInterface>(context: Context) : ServiceConnection {

    abstract val connectionAction: String

    private val contextReference = WeakReference<Context>(context)
    protected val packageManager: PackageManager?
        get() = contextReference.get()?.packageManager

    private val _service = atomic<T?>(null)
    val service: T?
        get() = _service.value

    private val _bound = MutableStateFlow(false)
    val bound = _bound.asStateFlow()

    val isBound: Boolean
        get() = bound.value

    private val _appInfo = MutableStateFlow<AppInfo?>(null)
    val appInfo = _appInfo.asStateFlow()

    val boundAppInfo: AppInfo?
        get() = appInfo.value

    val appPackageName: String?
        get() = boundAppInfo?.packageName?.ifBlank { null }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val bound = bind(service).also {
            this._service.value = it
        }
        val couldBind = _bound.updateAndGet { this.service != null }

        _appInfo.updateAndGet {
            if (couldBind) {
                val packageName = packageName(name)
                if (packageName.isNullOrBlank()) {
                    null
                } else {
                    val info = applicationInfo(packageName, packageManager)

                    AppInfo(
                        packageName = packageName,
                        name = info?.let {
                            scopeCatching {
                                packageManager?.getApplicationLabel(it)?.ifBlank { null }
                            }.getOrNull() ?: scopeCatching {
                                packageManager?.let { p -> it.loadLabel(p) }?.ifBlank { null }
                            }.getOrNull()
                        }?.toString() ?: packageName.substringAfterLast('.').toTitleCase(),
                        logo = info?.let {
                            scopeCatching {
                                packageManager?.getApplicationIcon(it)
                            }.getOrNull() ?: scopeCatching {
                                packageManager?.let { p -> it.loadIcon(p) }
                            }.getOrNull()
                        } ?: scopeCatching {
                            packageManager?.getApplicationIcon(packageName)
                        }.getOrNull() ?: name?.let {
                            scopeCatching {
                                packageManager?.getActivityIcon(it)
                            }.getOrNull()
                        }
                    )
                }
            } else {
                null
            }
        }

        if (bound != null) {
            onConnected(bound)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        _service.value = null
        val stillBound = _bound.updateAndGet { this.service != null }
        _appInfo.update {
            if (stillBound) it else null
        }
        onDisconnected()
    }

    protected fun packageName(
        name: ComponentName?,
        packageManager: PackageManager? = this.packageManager
    ): String? = scopeCatching {
        name?.packageName?.ifBlank { null }
    }.getOrNull() ?: scopeCatching {
        val uid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Binder.getCallingUidOrThrow()
        } else {
            Binder.getCallingUid()
        }
        packageManager?.getNameForUid(uid)?.ifBlank { null }
    }.getOrNull()

    abstract fun bind(service: IBinder?): T?
    abstract fun onConnected(service: T)
    abstract fun onDisconnected()

    companion object {

        internal const val EXTENSION_PACKAGE = "dev.datlag.mimasu.extension"

        /**
         * Get all available packageNames implementing the action.
         */
        @SuppressLint("WrongConstant")
        suspend fun extensions(packageManager: PackageManager, action: String): Set<String> {
            val intent = Intent(action)
            val resolveInfoList = dev.datlag.tooling.scopeCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.queryIntentServices(
                        intent,
                        PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
                    )
                } else {
                    packageManager.queryIntentServices(
                        intent,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PackageManager.MATCH_ALL
                        } else {
                            0
                        }
                    )
                }
            }.getOrNull() ?: return emptySet()

            return resolveInfoList.mapNotNull {
                it.serviceInfo?.packageName?.ifBlank { null } ?: it.resolvePackageName?.ifBlank { null }
            }.toSet()
        }

        fun bind(context: Context, service: AIDLService<*>, packageName: String): Boolean {
            val bindIntent = Intent(service.connectionAction).apply {
                setPackage(packageName)
            }
            val couldBind = scopeCatching {
                context.bindService(bindIntent, service, Context.BIND_AUTO_CREATE)
            }.getOrNull()

            return couldBind == true || service.isBound
        }

        fun unbind(context: Context, service: AIDLService<*>): Boolean {
            return scopeCatching {
                context.unbindService(service)
            }.isSuccess
        }

        private fun applicationInfo(
            packageName: String,
            packageManager: PackageManager?
        ): ApplicationInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager?.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                packageManager?.getApplicationInfo(packageName, 0)
            }
        }.getOrNull()

        fun extensionInstalled(context: Context): Boolean = applicationInfo(EXTENSION_PACKAGE, context.packageManager) != null

        @SuppressLint("WrongConstant")
        fun openExtension(context: Context) {
            fun activities(intent: Intent): List<ResolveInfo> {
                val resolved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager?.queryIntentActivities(
                        intent,
                        PackageManager.ResolveInfoFlags.of(PackageManager.GET_RESOLVED_FILTER.toLong())
                    )
                } else {
                    context.packageManager?.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER)
                }
                return resolved.orEmpty().filterNotNull()
            }

            val foundLaunchIntent = if (Platform.isTelevision(context)) {
                context.packageManager?.getLeanbackLaunchIntentForPackage(EXTENSION_PACKAGE)
            } else {
                context.packageManager?.getLaunchIntentForPackage(EXTENSION_PACKAGE)
            }
            val launchIntent = foundLaunchIntent ?: extensionInstalled(context).takeIf { it }?.let {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    setPackage(EXTENSION_PACKAGE)
                }

                activities(intent).firstOrNull()?.let { activity ->
                    intent.setClassName(EXTENSION_PACKAGE, activity.activityInfo.name)
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            } ?: return

            context.startActivity(launchIntent)
        }

        fun extensionStorageSettings(context: Context) {
            if (extensionInstalled(context)) {
                val uri = Uri.fromParts("package", EXTENSION_PACKAGE, null)
                val intent = Intent(Intent.ACTION_MANAGE_PACKAGE_STORAGE, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    context.startActivity(fallbackIntent)
                }
            }
        }
    }
}