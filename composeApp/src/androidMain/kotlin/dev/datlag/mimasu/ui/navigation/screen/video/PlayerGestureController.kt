package dev.datlag.mimasu.ui.navigation.screen.video

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.os.postDelayed
import dev.datlag.tooling.scopeCatching
import kotlin.math.abs

class PlayerGestureController(
    private val context: Context,
    private val listener: PlayerGestureOptions
) : View.OnTouchListener {

    private val handler = Handler(Looper.getMainLooper())

    private val elapsedTime: Long
        get() = SystemClock.elapsedRealtime()

    private val gestureDetector = GestureDetector(context, GestureListener(), handler)
    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleGestureListener(), handler)

    var isEnabled = true
    var wasClick = true

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        scopeCatching {
            scaleGestureDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
        }

        return true
    }

    private inner class ScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener {
        var scaleFactor = 1F

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            wasClick = false
            scaleFactor *= detector.scaleFactor
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            when {
                scaleFactor < 0.8 -> listener.onMinimize()
                scaleFactor > 1.2 -> listener.onZoom()
            }
            scaleFactor = 1F
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private var lastClick = 0L
        private var lastDoubleClick = 0L

        override fun onDown(e: MotionEvent): Boolean {
            // Initially assume this event is for click
            wasClick = true

            if (scaleGestureDetector.isInProgress) {
                return false
            }

            val (width, _) = listener.getViewMeasures()
            if (isEnabled && isSecondClick()) {
                handler.removeCallbacksAndMessages(SINGLE_TAP_TOKEN)
                lastDoubleClick = elapsedTime
                val eventPositionPercentageX = e.x / width

                when {
                    eventPositionPercentageX < 0.4 -> listener.onDoubleTapLeftScreen()
                    eventPositionPercentageX > 0.6 -> listener.onDoubleTapRightScreen()
                    else -> listener.onDoubleTapCenterScreen()
                }
            } else {
                if (recentDoubleClick()) {
                    return true
                }
                handler.removeCallbacksAndMessages(SINGLE_TAP_TOKEN)
                handler.postDelayed(MAX_TIME_DIFF, SINGLE_TAP_TOKEN) {
                    if (!wasClick || isSecondClick()) {
                        return@postDelayed
                    }
                    listener.onSingleTap()
                }
                lastClick = elapsedTime
            }

            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (!isEnabled || scaleGestureDetector.isInProgress) {
                return false
            }

            val (width, height) = listener.getViewMeasures()
            val insideThreshHold = abs(e2.y - e1!!.y) <= MOVEMENT_THRESHOLD
            val insideBorder = (e1.x < BORDER_THRESHOLD || e1.y < BORDER_THRESHOLD || e1.x > width - BORDER_THRESHOLD || e1.y > height - BORDER_THRESHOLD)

            if (insideThreshHold || insideBorder || abs(distanceX) > abs(distanceY)) {
                return false
            }

            wasClick = false

            when {
                e1.x < width * 0.4 -> listener.onSwipeLeftScreen(distanceY)
                e1.x > width * 0.6 -> listener.onSwipeRightScreen(distanceY)
            }
            return true
        }

        private fun isSecondClick(): Boolean {
            return elapsedTime - lastClick < MAX_TIME_DIFF
        }

        private fun recentDoubleClick(): Boolean {
            return elapsedTime - lastDoubleClick < MAX_TIME_DIFF / 2
        }
    }

    companion object {
        private const val SINGLE_TAP_TOKEN = "singleTap"

        private const val MAX_TIME_DIFF = 400L
        private const val MOVEMENT_THRESHOLD = 30
        private const val BORDER_THRESHOLD = 90
    }
}