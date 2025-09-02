package dev.datlag.mimasu

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dev.datlag.mimasu.extension.AppInstallReceiver
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.tooling.safeCast
import dev.datlag.tooling.scopeCatching
import io.tolgee.Tolgee
import io.tolgee.TolgeeAndroid
import io.tolgee.TolgeeContextWrapper
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instanceOrNull
import kotlin.reflect.safeCast

open class MimasuActivity : ComponentActivity() {

    private val appInstallReceiver = AppInstallReceiver()

    protected val appContext: Context
        get() = scopeCatching {
            applicationContext
        }.getOrNull() ?: scopeCatching {
            baseContext
        }.getOrNull() ?: this

    protected fun di(context: Context? = appContext): DI? {
        return context?.safeCast<DIAware>()?.di
            ?: DIAware::class.safeCast(context)?.di
            ?: appContext.safeCast<DIAware>()?.di
            ?: application.safeCast<DIAware>()?.di
            ?: DIAware::class.safeCast(appContext)?.di
            ?: DIAware::class.safeCast(application)?.di
    }

    override fun attachBaseContext(newBase: Context?) {
        val instance = di(newBase)?.instanceOrNull<Tolgee>()?.let {
            val value by it
            value ?: Tolgee.instanceOrNull
        } ?: Tolgee.instanceOrNull

        val wrapper = if (instance != null) {
            TolgeeContextWrapper.wrap(newBase, instance)
        } else {
            TolgeeContextWrapper.wrap(newBase)
        }

        super.attachBaseContext(wrapper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tolgeeInstance = di()?.instanceOrNull<Tolgee>()?.let {
            val value by it
            value ?: Tolgee.instanceOrNull
        } ?: Tolgee.instanceOrNull

        (tolgeeInstance as? TolgeeAndroid)?.preload(this)
    }

    open fun bindExtension(predicate: () -> Boolean) {
        if (predicate()) {
            ExtensionInitializer.rebindIfNoneAvailable(lifecycleScope, this)
        }
    }

    open fun registerExtension(predicate: () -> Boolean): Boolean {
        return if (predicate()) {
            val intentFilter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addDataScheme("package")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                scopeCatching {
                    registerReceiver(appInstallReceiver, intentFilter, RECEIVER_EXPORTED)
                }.isSuccess
            } else {
                scopeCatching {
                    registerReceiver(appInstallReceiver, intentFilter)
                }.isSuccess
            }
        } else {
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        scopeCatching {
            unregisterReceiver(appInstallReceiver)
        }.isSuccess
        ExtensionInitializer.unbindAll(this)
    }
}