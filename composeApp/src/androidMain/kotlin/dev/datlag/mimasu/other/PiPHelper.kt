package dev.datlag.mimasu.other

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.util.Rational
import dev.datlag.mimasu.common.findActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PiPHelper(
    private val activity: Activity
) {

    constructor(context: Context) : this(context.findActivity()!!)

    private val packageManager: PackageManager
        get() = activity.packageManager

    fun support(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageManager.hasSystemFeature(
                PackageManager.FEATURE_PICTURE_IN_PICTURE
            ) || if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.hasSystemFeature(PackageManager.FEATURE_EXPANDED_PICTURE_IN_PICTURE)
            } else {
                false
            }
        } else {
            false
        }
    }

    fun params(aspectRatio: Float, sourceRectHint: Rect): PictureInPictureParams? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PictureInPictureParams.Builder()
                .setAspectRatio(convertFloatToRational(aspectRatio))
                .setSourceRectHint(sourceRectHint)
                .also {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        it.setSeamlessResizeEnabled(true)
                    }
                }
                .build()
        } else {
            null
        }
    }

    fun enter(aspectRatio: Float, sourceRectHint: Rect) = enter(params(aspectRatio, sourceRectHint))

    fun enter(params: PictureInPictureParams?) {
        fun fallback() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                activity.enterPictureInPictureMode()
            }
        }

        if (params != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.enterPictureInPictureMode(params)
            } else {
                fallback()
            }
        } else {
            fallback()
        }
    }

    private fun convertFloatToRational(number: Float): Rational {
        if (number.isNaN()) {
            return Rational.NaN
        }

        val bits = number.toBits()
        val sign = bits ushr 31
        val exponent = ((bits ushr 23) xor (sign shl 7)) - 127
        val fraction = bits shl 8

        var a = 1
        var b = 1

        for (i in 30 downTo 8) {
            a = a * 2 + ((fraction ushr i) and 1)
            b *= 2
        }

        if (exponent > 0) {
            a *= (1 shl exponent)
        } else {
            b *= (1 shl -exponent)
        }

        if (sign == 1) {
            a *= -1
        }

        return Rational(a, b)
    }

    companion object {
        private val _active = MutableStateFlow(false)
        val active = _active.asStateFlow()

        private var enterListener: (() -> Unit)? = null

        fun setActive(value: Boolean = true) {
            _active.update { value }
        }

        fun onEnter(listener: () -> Unit) {
            enterListener = listener
        }

        fun registerEnter() {
            enterListener?.invoke()
        }

        fun clearEnter() {
            enterListener = null
        }
    }
}