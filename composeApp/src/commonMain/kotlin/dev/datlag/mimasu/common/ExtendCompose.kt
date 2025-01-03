package dev.datlag.mimasu.common

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePainter.State
import coil3.compose.rememberAsyncImagePainter
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.mimasu.LocalHaze
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

@Composable
fun PaddingValues.merge(other: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current

    return PaddingValues(
        start = max(this.calculateStartPadding(direction), other.calculateStartPadding(direction)),
        top = max(this.calculateTopPadding(), other.calculateTopPadding()),
        end = max(this.calculateEndPadding(direction), other.calculateEndPadding(direction)),
        bottom = max(this.calculateBottomPadding(), other.calculateBottomPadding())
    )
}

@Composable
fun PaddingValues.merge(all: Dp): PaddingValues {
    val direction = LocalLayoutDirection.current
    val other = PaddingValues(all)

    return PaddingValues(
        start = max(this.calculateStartPadding(direction), other.calculateStartPadding(direction)),
        top = max(this.calculateTopPadding(), other.calculateTopPadding()),
        end = max(this.calculateEndPadding(direction), other.calculateEndPadding(direction)),
        bottom = max(this.calculateBottomPadding(), other.calculateBottomPadding())
    )
}

@Composable
fun Modifier.hazeChild(
    state: HazeState = LocalHaze.current,
    style: HazeStyle = HazeMaterials.thin(),
    listState: LazyListState
) = Modifier.hazeChild(
    state = state,
    style = style
) {
    alpha = if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
        0F
    } else {
        1F
    }
}

/**
 * Return true/null per event to consume it, false otherwise.
 * Consuming results in no further handling of next key event listeners.
 */
@Composable
fun Modifier.handleDPadKeyEvents(
    onLeft: (() -> Any)? = null,
    onRight: (() -> Any)? = null,
    onUp: (() -> Any)? = null,
    onDown: (() -> Any)? = null,
    onEnter: (() -> Any)? = null,
): Modifier = onKeyEvent {
    if (it.type == KeyEventType.KeyUp) {
        when (it.key) {
            Key.DirectionLeft, Key.SystemNavigationLeft -> {
                onLeft?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
            Key.DirectionRight, Key.SystemNavigationRight -> {
                onRight?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
            Key.DirectionUp, Key.SystemNavigationUp -> {
                onUp?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
            Key.DirectionDown, Key.SystemNavigationDown -> {
                onDown?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
            Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                onEnter?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
        }
    }
    false
}

/**
 * Return true/null per event to consume it, false otherwise.
 * Consuming results in no further handling of next key event listeners.
 */
@Composable
fun Modifier.handlePlayerShortcuts(
    play: (() -> Any)? = null,
    playPause: (() -> Any)? = null,
    pause: (() -> Any)? = null,
    rewind: (() -> Any)? = null,
    forward: (() -> Any)? = null,
): Modifier = onKeyEvent {
    if (it.type == KeyEventType.KeyUp) {
        when (it.key) {
            Key.MediaPlay -> {
                play?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
            Key.MediaPlayPause, Key.K, Key.Spacebar -> {
                playPause?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
            Key.MediaPause -> {
                pause?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
            Key.MediaRewind, Key.J -> {
                rewind?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
            Key.MediaFastForward, Key.L -> {
                forward?.invoke().also { consume -> return@onKeyEvent (consume as? Boolean) ?: true }
            }
        }
    }
    false
}

@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.isFullyExpandedOrTargeted(forceFullExpand: Boolean = false): Boolean {
    val checkState = if (this.hasExpandedState) {
        SheetValue.Expanded
    } else {
        if (forceFullExpand) {
            return false
        }
        SheetValue.PartiallyExpanded
    }

    return this.currentValue == checkState || this.targetValue == checkState
}

fun CornerSize.times(value: Float, density: Density, shapeSize: Size = Size.Unspecified): CornerSize {
    return CornerSize(this.toPx(shapeSize, density) * value)
}

@Composable
fun rememberNestedImagePainter(
    models: Collection<Any?>,
    contentScale: ContentScale = ContentScale.Crop,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
): AsyncImagePainter {
    val data = remember(models) { models.filterNotNull() }

    if (data.isEmpty()) {
        return rememberAsyncImagePainter(
            model = null,
            contentScale = contentScale
        )
    }

    return rememberAsyncImagePainter(
        model = data.first(),
        contentScale = contentScale,
        error = rememberNestedImagePainter(data.drop(1), contentScale),
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = {
            if (data.size <= 1) {
                onError?.invoke(it)
            }
        }
    )
}

val LazyListState.canScroll: Boolean
    get() = this.canScrollForward || this.canScrollBackward

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    return if (this.canScroll) {
        var previousIndex by remember(this) {
            mutableStateOf(firstVisibleItemIndex)
        }
        var previousScrollOffset by remember(this) {
            mutableStateOf(firstVisibleItemScrollOffset)
        }
        remember(this) {
            derivedStateOf {
                if (previousIndex != firstVisibleItemIndex) {
                    previousIndex > firstVisibleItemIndex
                } else {
                    previousScrollOffset >= firstVisibleItemScrollOffset
                }.also {
                    previousIndex = firstVisibleItemIndex
                    previousScrollOffset = firstVisibleItemScrollOffset
                }
            }
        }.value && canScrollForward
    } else {
        true
    }
}