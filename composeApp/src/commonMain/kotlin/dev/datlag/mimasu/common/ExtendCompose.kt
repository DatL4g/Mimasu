package dev.datlag.mimasu.common

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

suspend fun PointerInputScope.detectPinchGestures(
    pass: PointerEventPass = PointerEventPass.Main,
    onGestureStart: (PointerInputChange) -> Unit = { },
    onGesture: (centroid: Offset, zoom: Float) -> Unit,
    onGestureEnd: (PointerInputChange) -> Unit = { }
) {
    awaitEachGesture {
        var zoom = 1F
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        val down = awaitFirstDown(requireUnconsumed = false, pass = pass)

        onGestureStart(down)

        var pointer = down
        var pointerId = down.id

        do {
            val event = awaitPointerEvent(pass = pass)
            val canceled = event.changes.any { it.isConsumed }

            if (!canceled) {
                val pointerInputChange = event.changes.firstOrNull {
                    it.id == pointerId
                } ?: event.changes.first()

                pointerId = pointerInputChange.id
                pointer = pointerInputChange

                val zoomChange = event.calculateZoom()

                if (!pastTouchSlop) {
                    zoom *= zoomChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1F - zoom) * centroidSize

                    if (zoomMotion > touchSlop) {
                        pastTouchSlop = true
                    }
                }

                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)

                    if (zoomChange != 1F) {
                        onGesture(
                            centroid,
                            zoomChange
                        )
                        event.changes.forEach { it.consume() }
                    }
                }
            }
        } while (!canceled && event.changes.any { it.pressed })

        onGestureEnd(pointer)
    }
}

suspend fun PointerInputScope.detectSingleTap(
    pass: PointerEventPass = PointerEventPass.Main,
    onSingleTap: (Offset) -> Unit
) = coroutineScope {
    awaitEachGesture {
        // Wait for the first touch event
        val down: PointerInputChange = awaitFirstDown(pass = pass)

        // Capture the position of the down event
        val downPosition = down.position
        val touchSlop = viewConfiguration.touchSlop
        var isSingleTap = true

        do {
            val event = awaitPointerEvent(pass = pass)
            val pointer = event.changes.first()

            // Check if there’s movement beyond the touch slop, which would disqualify it as a single tap
            if (abs(pointer.position.x - downPosition.x) > touchSlop ||
                abs(pointer.position.y - downPosition.y) > touchSlop
            ) {
                isSingleTap = false
            }

            // If the pointer has been lifted, check if it's still a valid single tap
            if (pointer.changedToUp()) {
                if (isSingleTap) {
                    onSingleTap(downPosition)
                }
                break
            }

        } while (event.changes.any { it.pressed })
    }
}