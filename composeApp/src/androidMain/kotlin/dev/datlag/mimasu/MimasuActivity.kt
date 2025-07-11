package dev.datlag.mimasu

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dev.datlag.mimasu.extension.AppInstallReceiver
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.tooling.scopeCatching

open class MimasuActivity : ComponentActivity() {

    private val appInstallReceiver = AppInstallReceiver()

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