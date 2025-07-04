package dev.datlag.mimasu

import android.app.PictureInPictureUiState
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.kermit.Logger
import dev.datlag.kast.Kast
import dev.datlag.mimasu.common.isInPiPMode
import dev.datlag.mimasu.common.toExpressiveTypography
import dev.datlag.mimasu.extension.AppInstallReceiver
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.other.AdManager
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.tv.TVActivity
import dev.datlag.mimasu.ui.theme.Font
import dev.datlag.mimasu.ui.viewmodel.LoginViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.safeCast
import dev.datlag.tooling.scopeCatching
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

        if (Platform.isTelevision(this)) {
            val intent = Intent(this, TVActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            installSplashScreen().apply {
                setKeepOnScreenCondition {
                    NetworkModule.showSplashscreen
                }
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val di = this.di ?: return exit("Could not find dependency injection.")
        val nullableAdManager by di.instanceOrNull<AdManager>()
        (nullableAdManager ?: AdManager(this)).requestConsentUpdate(this)
        ExtensionInitializer.rebindIfNoneAvailable(lifecycleScope, this)
        PiPHelper.setActive(this.isInPiPMode())
        Kast.setup(this)

        setContent {
            App(
                di = di,
                typography = Font.manrope.toExpressiveTypography(),
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

        ExtensionInitializer.rebindIfNoneAvailable(lifecycleScope, this)
        PiPHelper.setActive(this.isInPiPMode())
    }

    override fun onResume() {
        super.onResume()

        ExtensionInitializer.rebindIfNoneAvailable(lifecycleScope, this)
        PiPHelper.setActive(this.isInPiPMode())
    }

    override fun onPause() {
        super.onPause()

        ExtensionInitializer.rebindIfNoneAvailable(lifecycleScope, this)
        PiPHelper.setActive(this.isInPiPMode())
    }

    override fun onRestart() {
        super.onRestart()

        ExtensionInitializer.rebindIfNoneAvailable(lifecycleScope, this)
        PiPHelper.setActive(this.isInPiPMode())
    }

    override fun onDestroy() {
        super.onDestroy()

        scopeCatching {
            unregisterReceiver(appInstallReceiver)
        }.isSuccess
        ExtensionInitializer.unbindAll(this)
        PiPHelper.setActive(this.isInPiPMode())
        Kast.dispose()
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

    override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
        super.onPictureInPictureUiStateChanged(pipState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            PiPHelper.setActive(pipState.isTransitioningToPip || this.isInPiPMode())
        }
    }
}