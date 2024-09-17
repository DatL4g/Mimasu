package dev.datlag.mimasu.other

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import io.github.aakira.napier.Napier
import kotlin.math.max
import kotlin.math.min

class BrightnessHelper(
    private val activity: Activity
) {
    private val window: Window?
        get() = activity.window

    private val minBrightness = 0.0f
    private val maxBrightness = 1.0f

    /**
     * Wrapper for the current screen brightness
     */
    private var brightness: Float
        get() = min(max(window?.attributes?.screenBrightness ?: minBrightness, minBrightness), maxBrightness)
        set(value) {
            window?.attributes?.screenBrightness = min(max(value, minBrightness), maxBrightness)
        }

    /**
     * Restore screen brightness to device system brightness.
     * if [forced] is false then value will be stored only if it's not
     * [WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE] value.
     */
    fun resetToSystemBrightness() {
        brightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    }

    fun incrementBrightness(step: Int) {
        Napier.e("Current Brightness: $brightness")
        brightness += (step.toFloat() / 100F)
        Napier.e("New Brightness: $brightness")
        // setBrightnessWithScale(brightness + step)
    }
}