package dev.datlag.mimasu

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.backhandler.backHandler
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import dev.datlag.kast.Kast
import dev.datlag.mimasu.common.isActivityInPiPMode
import dev.datlag.mimasu.core.MimasuConnection
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.other.PackageResolver
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.navigation.RootComponent
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.decompose.lifecycle.LocalLifecycleOwner
import dev.datlag.tooling.safeCast
import dev.datlag.tooling.scopeCatching
import org.kodein.di.DIAware

class MainActivity : ComponentActivity() {

    private lateinit var root: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                NetworkModule.showSplashscreen
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val di = applicationContext.safeCast<DIAware>()?.di ?: (application as DIAware).di
        val lifecycleOwner = object : LifecycleOwner {
            override val lifecycle: Lifecycle = essentyLifecycle()
        }

        root = RootComponent(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycleOwner.lifecycle,
                backHandler = backHandler()
            ),
            di = di
        )

        Kast.setup(this)
        PackageResolver.bindUpdate(this)
        PiPHelper.setActive(this.isActivityInPiPMode())
        
        setContent { 
            CompositionLocalProvider(
                LocalLifecycleOwner provides lifecycleOwner,
                LocalEdgeToEdge provides true
            ) {
                App(
                    di = di,
                    fetchingContent = {
                        PlatformText(text = "Fetching config please wait")
                    },
                    failureContent = {
                        PlatformText(text = "Failure: $it")
                    },
                    maintenanceContent = {
                        PlatformText(text = "App currently under maintenance, please come back later")
                    }
                ) {
                    root.render()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        PackageResolver.bindUpdate(this)
        PiPHelper.setActive(this.isActivityInPiPMode())
    }

    override fun onResume() {
        super.onResume()

        PackageResolver.bindUpdate(this)
        PiPHelper.setActive(this.isActivityInPiPMode())
    }

    override fun onPause() {
        super.onPause()

        PiPHelper.setActive(this.isActivityInPiPMode())
    }

    override fun onRestart() {
        super.onRestart()

        PiPHelper.setActive(this.isActivityInPiPMode())
    }

    override fun onDestroy() {
        super.onDestroy()

        scopeCatching {
            unbindService(MimasuConnection.Update)
        }.getOrNull()
        PiPHelper.setActive(this.isActivityInPiPMode())
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        PiPHelper.registerEnter()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        PiPHelper.setActive(isInPictureInPictureMode)
    }
}