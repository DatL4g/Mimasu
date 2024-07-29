package dev.datlag.mimasu

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
import dev.datlag.mimasu.ui.navigation.RootComponent
import dev.datlag.tooling.decompose.lifecycle.LocalLifecycleOwner
import dev.datlag.tooling.safeCast
import org.kodein.di.DIAware

class MainActivity : ComponentActivity() {

    private lateinit var root: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

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
        
        setContent { 
            CompositionLocalProvider(
                LocalLifecycleOwner provides lifecycleOwner,
                LocalEdgeToEdge provides true
            ) {
                App(
                    di = di
                ) {
                    root.render()
                }
            }
        }
    }
}