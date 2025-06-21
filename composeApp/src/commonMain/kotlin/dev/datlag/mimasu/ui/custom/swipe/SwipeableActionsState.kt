package dev.datlag.mimasu.ui.custom.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Stable
class SwipeableActionsState internal constructor() {

    private var offsetState = mutableStateOf(0F)

    val offset: State<Float>
        get() = offsetState

    internal var swipedAction: SwipeActionMeta? by mutableStateOf(null)

    val isResettingOnRelease: Boolean by derivedStateOf {
        swipedAction != null
    }

    internal var layoutWidth: Int by mutableIntStateOf(0)
    internal var swipeThresholdPx: Float by mutableFloatStateOf(0F)
    internal val ripple = SwipeRippleState()

    internal var actions: ActionFinder by mutableStateOf(
        ActionFinder(left = emptyList(), right = emptyList())
    )

    internal val visibleAction: SwipeActionMeta? by derivedStateOf {
        actions.actionAt(offset.value, totalWidth = layoutWidth)
    }

    internal val draggableState = DraggableState { delta ->
        val targetOffset = offsetState.value + delta
        val canSwipeTowardsRight = actions.left.isNotEmpty()
        val canSwipeTowardsLeft = actions.right.isNotEmpty()

        val isAllowed = isResettingOnRelease
                || targetOffset == 0f
                || (targetOffset > 0F && canSwipeTowardsRight)
                || (targetOffset < 0F && canSwipeTowardsLeft)

        offsetState.value += if (isAllowed) delta else delta / 10
    }

    internal fun hasCrossedSwipeThreshold(): Boolean {
        return abs(offset.value) > swipeThresholdPx
    }

    internal suspend fun handleOnDragStopped() = coroutineScope {
        launch {
            if (hasCrossedSwipeThreshold()) {
                visibleAction?.let { action ->
                    swipedAction = action
                    action.value.onSwipe()
                    ripple.animate(actionMeta = action)
                }
            }
        }
        launch {
            draggableState.drag(MutatePriority.PreventUserInput) {
                Animatable(initialValue = offset.value).animateTo(
                    targetValue = 0F,
                    animationSpec = tween(durationMillis = 4_00)
                ) {
                    dragBy(value - offset.value)
                }
            }
            swipedAction = null
        }
    }
}

@Composable
fun rememberSwipeableActionsState(): SwipeableActionsState {
    return remember { SwipeableActionsState() }
}