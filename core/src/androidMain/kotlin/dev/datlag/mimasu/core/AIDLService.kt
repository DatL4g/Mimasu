package dev.datlag.mimasu.core

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.IInterface
import dev.datlag.mimasu.core.model.AppInfo
import dev.datlag.tooling.scopeCatching
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import java.lang.ref.WeakReference

abstract class AIDLService<T : IInterface>(context: Context) : ServiceConnection {

    abstract val connectionAction: String

    private val contextReference = WeakReference<Context>(context)
    protected val packageManager: PackageManager?
        get() = contextReference.get()?.packageManager

    var service: T? = null
        private set

    private val _bound = MutableStateFlow(false)
    val bound: StateFlow<Boolean> = _bound.asStateFlow()

    val isBound: Boolean
        get() = bound.value

    private val _appInfo = MutableStateFlow<AppInfo?>(null)
    val appInfo: StateFlow<AppInfo?> = _appInfo.asStateFlow()

    val boundAppInfo: AppInfo?
        get() = appInfo.value

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val bound = bind(service).also {
            this.service = it
        }
        val couldBind = _bound.updateAndGet { this.service != null }

        _appInfo.updateAndGet {
            if (couldBind) {
                val packageName = packageName(name)
                if (packageName.isNullOrBlank()) {
                    null
                } else {
                    val info = applicationInfo(packageName)

                    AppInfo(
                        packageName = packageName,
                        name = info?.let {
                            scopeCatching {
                                packageManager?.getApplicationLabel(it)?.ifBlank { null }
                            }.getOrNull() ?: scopeCatching {
                                packageManager?.let { p -> it.loadLabel(p) }?.ifBlank { null }
                            }.getOrNull()
                        }?.toString(),
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
        service = null
        val stillBound = _bound.updateAndGet { this.service != null }
        _appInfo.update {
            if (stillBound) it else null
        }
        onDisconnected()
    }

    protected fun applicationInfo(
        packageName: String,
        packageManager: PackageManager? = this.packageManager
    ): ApplicationInfo? = scopeCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager?.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
        } else {
            packageManager?.getApplicationInfo(packageName, 0)
        }
    }.getOrNull()

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


}