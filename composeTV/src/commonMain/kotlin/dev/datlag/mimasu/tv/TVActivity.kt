package dev.datlag.mimasu.tv

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.tv.material3.Text
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.ui.other.Network
import dev.datlag.tooling.safeCast
import org.kodein.di.DI
import org.kodein.di.DIAware
import kotlin.reflect.safeCast

class TVActivity : ComponentActivity() {

    private val di: DI?
        get() = applicationContext.safeCast<DIAware>()?.di
            ?: application.safeCast<DIAware>()?.di
            ?: DIAware::class.safeCast(applicationContext)?.di
            ?: DIAware::class.safeCast(application)?.di

    override fun onCreate(savedInstanceState: Bundle?) {
        fun exit(reason: String?) {
            reason?.let { Logger.e(messageString = it) }
            finishAffinity()
        }

        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                Network.showSplashscreen
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val di = this.di ?: return exit("Could not find dependency injection.")

        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.aligned(Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hello on Android TV",
                    color = Color.White
                )
            }
        }
    }
}