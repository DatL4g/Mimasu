package dev.datlag.mimasu

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.extension.AppInstallReceiver
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.other.AdManager
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.login.Login
import dev.datlag.mimasu.ui.theme.Font
import dev.datlag.mimasu.ui.viewmodel.AccountViewModel
import dev.datlag.mimasu.ui.viewmodel.LoginViewModel
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.typography
import dev.datlag.tooling.compose.toTypography
import dev.datlag.tooling.safeCast
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instanceOrNull
import kotlin.reflect.safeCast

class MainActivity : AdActivity() {

    private val di: DI?
        get() = applicationContext.safeCast<DIAware>()?.di
            ?: application.safeCast<DIAware>()?.di
            ?: DIAware::class.safeCast(applicationContext)?.di
            ?: DIAware::class.safeCast(application)?.di

    private val appInstallReceiver = AppInstallReceiver()

    // ToDo("use Tolgee wrapper")
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        fun exit(reason: String?) {
            reason?.let { Logger.e(messageString = it) }
            finishAffinity()
        }

        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                NetworkModule.showSplashscreen
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val di = this.di ?: return exit("Could not find dependency injection.")
        val nullableAdManager by di.instanceOrNull<AdManager>()
        (nullableAdManager ?: AdManager(this)).requestConsentUpdate(this)

        setContent {
            // ToDo("ignore font on TV")
            // ToDo("navigation wrapped for TV")
            App(
                di = di,
                typography = Font.manrope.toTypography(),
                fetchingContent = {
                    PlatformText("Fetching Config, please wait")
                },
                failureContent = {
                    PlatformText("Report Failure: $it")
                },
                content = {
                    Navigation()
                }
            )
        }

        handleIntent(intent)
    }

    override fun onStart() {
        super.onStart()

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(appInstallReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(appInstallReceiver, intentFilter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(appInstallReceiver)
        ExtensionInitializer.unbindAll(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val action = intent?.action?.ifBlank { null }

        if (Intent.ACTION_VIEW == action) {
            val data = intent.data

            if (data != null) {
                val oobCode = data.getQueryParameter("oobCode")?.ifBlank { null }
                val mode = data.getQueryParameter("mode")?.ifBlank { null }

                if (mode.equals("resetPassword", ignoreCase = true) && !oobCode.isNullOrBlank()) {
                    LoginViewModel.setResetCode(oobCode)
                }
            }
        }
        setIntent(Intent())
    }

}