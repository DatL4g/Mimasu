package dev.datlag.mimasu.other

import android.app.Activity
import android.content.Context
import android.view.Window
import android.view.WindowManager
import dev.datlag.mimasu.common.findActivity
import io.github.aakira.napier.Napier
import kotlin.math.max
import kotlin.math.min

class BrightnessHelper(
    private val activity: Activity?
) {

    constructor(context: Context) : this(context.findActivity())

    private val window: Window?
        get() = activity?.window

    private val minBrightness = 0.0f
    private val maxBrightness = 1.0f

    /**
     * Wrapper for the current screen brightness
     */
    var brightness: Float
        get() = window?.attributes?.screenBrightness?.coerceIn(minBrightness, maxBrightness) ?: minBrightness
        set(value) {
            val newBrightness = if (value == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
                value
            } else {
                value.coerceIn(minBrightness, maxBrightness)
            }

            val layoutParams = window?.attributes?.apply {
                screenBrightness = newBrightness
            } ?: return

            window?.attributes = layoutParams
        }

    private val initialBrightness = brightness

    fun dispose() {
        brightness = initialBrightness
        brightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    }
}