package dev.datlag.mimasu

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.backhandler.backHandler
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import dev.datlag.kast.Kast
import dev.datlag.mimasu.common.clear
import dev.datlag.mimasu.common.isActivityInPiPMode
import dev.datlag.mimasu.core.Constants
import dev.datlag.mimasu.core.MimasuConnection
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.other.PackageResolver
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.other.Shortcuts
import dev.datlag.mimasu.ui.navigation.RootComponent
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.decompose.lifecycle.LocalLifecycleOwner
import dev.datlag.tooling.safeCast
import dev.datlag.tooling.scopeCatching
import org.kodein.di.DIAware

class MainActivity : ComponentActivity() {

    private lateinit var root: RootComponent

    @OptIn(ExperimentalSharedTransitionApi::class)
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

        handleShortcutIntent(intent)

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
                    SharedTransitionLayout {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this
                        ) {
                            root.render()
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleShortcutIntent(intent)
    }

    private fun handleShortcutIntent(intent: Intent?) {
        intent?.also {
            val action = it.action?.ifBlank { null }
            val data by lazy {
                it.dataString?.ifBlank { null } ?: it.data?.toString()?.ifBlank { null }
            }

            when {
                action.equals(Shortcuts.ACTION_GITHUB, ignoreCase = true) -> {
                    it.clear()

                    Platform.openInBrowser(Constants.GITHUB_URL, this)
                }
            }
        }?.clear()
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

        MimasuConnection.Update.unbindAll(this)
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