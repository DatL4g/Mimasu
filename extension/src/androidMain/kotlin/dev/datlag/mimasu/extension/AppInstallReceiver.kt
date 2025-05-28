package dev.datlag.mimasu.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.Keep

@Keep
class AppInstallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when {
                intent.action.equals(Intent.ACTION_PACKAGE_ADDED) -> ExtensionInitializer.rebindAll(context)
                intent.action.equals(Intent.ACTION_PACKAGE_REPLACED) -> ExtensionInitializer.rebindAll(context)
                intent.action.equals(Intent.ACTION_PACKAGE_REMOVED) -> ExtensionInitializer.unbindAll(context)
            }
        }
    }

}