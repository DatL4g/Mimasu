package dev.datlag.mimasu.other

import android.app.Activity
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import dev.datlag.mimasu.ui.common.findActivity
import dev.datlag.mimasu.ui.common.findWindow
import dev.datlag.tooling.scopeCatching

class BrightnessHelper(
    private val activity: Activity?,
    private val window: Window? = activity?.window
) {

    val minBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF
    val maxBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL

    var brightness: Float
        get() = window?.attributes?.screenBrightness?.let {
            if (it == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
                systemBrightness
            } else {
                it
            }
        }?.coerceIn(minBrightness, maxBrightness) ?: systemBrightness
        set(value) {
            val newBrightness = if (value == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE || value < minBrightness) {
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            } else {
                value.coerceIn(minBrightness, maxBrightness)
            }

            val layoutParams = window?.attributes?.apply {
                screenBrightness = newBrightness
            } ?: return

            window.attributes = layoutParams
        }

    private val systemBrightness: Float
        get() = scopeCatching {
            val value = Settings.System.getInt(activity?.contentResolver, Settings.System.SCREEN_BRIGHTNESS)

            value.toFloat() / 255F
        }.getOrNull() ?: minBrightness

    private val initialBrightness = brightness

    fun dispose() {
        brightness = initialBrightness
        brightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    }
}

@Composable
fun rememberBrightnessHelper(
    view: View = LocalView.current,
    activity: Activity? = LocalActivity.current ?: view.context?.findActivity() ?: LocalContext.current.findActivity(),
    window: Window? = (view.parent as? DialogWindowProvider)?.window ?: view.context?.findWindow() ?: LocalContext.current.findWindow()
): BrightnessHelper {
    return remember(activity, window) {
        BrightnessHelper(activity, window)
    }
}