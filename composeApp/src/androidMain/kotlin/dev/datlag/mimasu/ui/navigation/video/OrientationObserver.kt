package dev.datlag.mimasu.ui.navigation.video

import android.annotation.SuppressLint
import android.content.ComponentCallbacks
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun rememberOrientation(): Int {
    val context = LocalContext.current
    val configuration = context.resources?.configuration ?: LocalConfiguration.current
    val fallbackConfiguration = LocalConfiguration.current
    var orientation by remember {
        mutableIntStateOf(configuration.orientation.takeIf { it > 0 } ?: fallbackConfiguration.orientation)
    }

    DisposableEffect(context) {
        val listener = object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                orientation = newConfig.orientation
            }

            override fun onLowMemory() { }
        }
        context.registerComponentCallbacks(listener)
        onDispose {
            context.unregisterComponentCallbacks(listener)
        }
    }

    return orientation
}