package dev.datlag.mimasu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.tooling.safeCast
import org.kodein.di.DIAware
import kotlin.reflect.safeCast

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        fun exit(reason: String?) {
            reason?.let { Logger.e(messageString = it) }
            finishAffinity()
        }

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val di = applicationContext.safeCast<DIAware>()?.di
            ?: application.safeCast<DIAware>()?.di
            ?: DIAware::class.safeCast(applicationContext)?.di
            ?: DIAware::class.safeCast(application)?.di
            ?: return exit("Could not find dependency injection.")

        setContent {
            App(
                di = di,
            ) {
                Navigation()
            }
        }
    }

}