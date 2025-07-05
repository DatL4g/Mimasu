package dev.datlag.mimasu.other

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.util.Rational
import android.view.View
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import dev.datlag.mimasu.common.isInPiPMode
import dev.datlag.mimasu.ui.common.findActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PiPHelper(
    private val activity: Activity?
) {

    private val packageManager: PackageManager?
        get() = activity?.packageManager

    private val _enabled = MutableStateFlow(supported())
    val enabled = _enabled.asStateFlow()

    fun enter(aspectRatio: Float, sourceRectHint: Rect) = enter(params(aspectRatio, sourceRectHint))

    fun enter(params: PictureInPictureParams?): Boolean {
        fun fallback(): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                activity?.enterPictureInPictureMode()
            }
            return activity?.isInPiPMode() ?: active.value
        }

        return if (params != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity?.enterPictureInPictureMode(params) ?: active.value
            } else {
                fallback()
            }
        } else {
            fallback()
        }
    }

    private fun params(aspectRatio: Float, sourceRectHint: Rect): PictureInPictureParams? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PictureInPictureParams.Builder()
                .setSourceRectHint(sourceRectHint.orNull())
                .apply {
                    val rational = convertFloatToRational(aspectRatio)
                    if (aspectRatio in MIN_STANDARD_RATIO..MAX_STANDARD_RATIO) {
                        setAspectRatio(rational)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            setExpandedAspectRatio(rational)
                        } else {
                            setAspectRatio(Rational(16, 9))
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        setSeamlessResizeEnabled(true)
                    }
                }
                .build()
        } else {
            null
        }
    }

    private fun supported(): Boolean {
        val pm = packageManager ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) || if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.hasSystemFeature(PackageManager.FEATURE_EXPANDED_PICTURE_IN_PICTURE)
            } else {
                false
            }
        } else {
            false
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

    private fun Rect.orNull(): Rect? {
        return if (this.isEmpty) {
            null
        } else {
            this
        }
    }

    companion object {
        private const val MIN_STANDARD_RATIO = 1F / 2.39F
        private const val MAX_STANDARD_RATIO = 2.39F

        private val _active = MutableStateFlow(false)
        val active = _active.asStateFlow()

        fun setActive(value: Boolean) = _active.update { value }
    }
}

@Composable
fun rememberPiPHelper(
    view: View = LocalView.current,
    activity: Activity? = LocalActivity.current ?: view.context?.findActivity() ?: LocalContext.current.findActivity()
): PiPHelper {
    SideEffect {
        activity?.isInPiPMode()?.let {
            PiPHelper.setActive(it)
        }
    }

    return remember(activity) { PiPHelper(activity) }
}