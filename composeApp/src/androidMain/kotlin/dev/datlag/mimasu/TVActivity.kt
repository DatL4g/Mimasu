package dev.datlag.mimasu

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.common.toExpressiveTypography
import dev.datlag.mimasu.tv.TVApp
import dev.datlag.mimasu.ui.navigation.login.rememberAppImage
import dev.datlag.mimasu.ui.other.Network
import dev.datlag.mimasu.ui.theme.Font
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.asTv
import dev.datlag.tooling.safeCast
import org.kodein.di.DI
import org.kodein.di.DIAware
import kotlin.reflect.safeCast

class TVActivity : MimasuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        fun exit(reason: String?) {
            reason?.let { Logger.Companion.e(messageString = it) }
            finishAffinity()
        }

        super.onCreate(savedInstanceState)
        if (Platform.isTelevision(this)) {
            installSplashScreen().apply {
                setKeepOnScreenCondition {
                    Network.showSplashscreen
                }
            }
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val di = this.di() ?: return exit("Could not find dependency injection.")
        bindExtension { Platform.isTelevision(this) }

        setContent {
            Column(
                modifier = Modifier.Companion.fillMaxSize(),
                verticalArrangement = Arrangement.aligned(Alignment.Companion.CenterVertically),
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            ) {
                TVApp(
                    di = di,
                    appImage = rememberAppImage(),
                    typography = Font.manrope.toExpressiveTypography().asTv(),
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()

        registerExtension { Platform.isTelevision(this) }
        bindExtension { Platform.isTelevision(this) }
    }

    override fun onResume() {
        super.onResume()

        bindExtension { Platform.isTelevision(this) }
    }

    override fun onPause() {
        super.onPause()

        bindExtension { Platform.isTelevision(this) }
    }

    override fun onRestart() {
        super.onRestart()

        bindExtension { Platform.isTelevision(this) }
    }
}