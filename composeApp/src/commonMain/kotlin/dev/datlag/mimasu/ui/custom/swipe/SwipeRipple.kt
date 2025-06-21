package dev.datlag.mimasu.ui.custom.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Stable
internal class SwipeRippleState {

    private var ripple = mutableStateOf<SwipeRipple?>(null)

    suspend fun animate(
        actionMeta: SwipeActionMeta
    ) {
        val drawOnRightSide = actionMeta.isOnRightSide
        val action = actionMeta.value

        ripple.value = SwipeRipple(
            isUndo = action.isUndo,
            rightSide = drawOnRightSide,
            color = action.background,
            alpha = 0f,
            progress = 0f
        )

        val animationDurationMs = (4_00 * (if (action.isUndo) 1.75F else 1F)).roundToInt()

        coroutineScope {
            launch {
                Animatable(initialValue = 0F).animateTo(
                    targetValue = 1F,
                    animationSpec = tween(durationMillis = animationDurationMs),
                    block = {
                        ripple.value = ripple.value?.copy(progress = value)
                    }
                )
            }
            launch {
                Animatable(initialValue = if (action.isUndo) 0F else 0.25F).animateTo(
                    targetValue = if (action.isUndo) 0.5F else 0F,
                    animationSpec = tween(
                        durationMillis = animationDurationMs,
                        delayMillis = if (action.isUndo) 0 else animationDurationMs / 2
                    ),
                    block = {
                        ripple.value = ripple.value?.copy(alpha = value)
                    }
                )
            }
        }
    }

    fun draw(scope: DrawScope) {
        ripple.value?.run {
            scope.clipRect {
                val size = scope.size
                val startRadius = if (isUndo) size.width + size.height else size.height
                val endRadius = if (!isUndo) size.width + size.height else size.height
                val radius = lerp(startRadius, endRadius, progress)

                drawCircle(
                    color = color,
                    radius = radius,
                    alpha = alpha,
                    center = this.center.copy(x = if (rightSide) this.size.width + this.size.height else 0F - this.size.height)
                )
            }
        }
    }

    private fun lerp(start: Float, stop: Float, fraction: Float) = (start * (1 - fraction) + stop * fraction)
}

private data class SwipeRipple(
    val isUndo: Boolean,
    val rightSide: Boolean,
    val color: Color,
    val alpha: Float,
    val progress: Float
)
