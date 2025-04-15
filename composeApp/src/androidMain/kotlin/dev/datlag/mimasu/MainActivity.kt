package dev.datlag.mimasu

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.login.Login
import dev.datlag.mimasu.ui.theme.Font
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.toTypography
import dev.datlag.tooling.safeCast
import org.kodein.di.DIAware
import kotlin.reflect.safeCast

class MainActivity : ComponentActivity() {

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

        val di = applicationContext.safeCast<DIAware>()?.di
            ?: application.safeCast<DIAware>()?.di
            ?: DIAware::class.safeCast(applicationContext)?.di
            ?: DIAware::class.safeCast(application)?.di
            ?: return exit("Could not find dependency injection.")

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
                    var logInResult by remember { mutableStateOf(false) }

                    Navigation(
                        isLoggedIn = logInResult,
                        loginContent = {
                            Login(
                                onSuccess = {
                                    logInResult = true
                                }
                            )
                        }
                    )
                }
            )
        }
    }

}