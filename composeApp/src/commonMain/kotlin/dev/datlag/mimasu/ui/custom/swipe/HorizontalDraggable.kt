package dev.datlag.mimasu.ui.custom.swipe

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlin.math.sign

internal fun Modifier.horizontalDraggable(
    state: DraggableState,
    enabled: Boolean = true,
    startDragImmediately: Boolean = false,
    onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit = { },
    onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit = { },
): Modifier = this then DraggableElement(
    state = state,
    enabled = enabled,
    startDragImmediately = { startDragImmediately },
    onDragStarted = onDragStarted,
    onDragStopped = { velocity -> onDragStopped(velocity.x) }
)

internal data class DraggableElement(
    private val state: DraggableState,
    private val enabled: Boolean,
    private val startDragImmediately: () -> Boolean,
    private val onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit,
    private val onDragStopped: suspend CoroutineScope.(velocity: Velocity) -> Unit
): ModifierNodeElement<DraggableNode>() {

    override fun create(): DraggableNode = DraggableNode(
        state = state,
        enabled = enabled,
        startDragImmediately = startDragImmediately,
        onDragStarted = onDragStarted,
        onDragStopped = onDragStopped
    )

    override fun update(node: DraggableNode) {
        node.update(
            state = state,
            enabled = enabled,
            startDragImmediately = startDragImmediately,
            onDragStarted = onDragStarted,
            onDragStopped = onDragStopped
        )
    }
}

internal class DraggableNode(
    private var state: DraggableState,
    private var enabled: Boolean,
    private var startDragImmediately: () -> Boolean,
    private var onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit,
    private var onDragStopped: suspend CoroutineScope.(velocity: Velocity) -> Unit
): DelegatingNode(), PointerInputModifierNode {

    private val velocityTracker = VelocityTracker()
    private val channel = Channel<DragEvent>(capacity = Channel.UNLIMITED)

    private val pointerInputNode = SuspendingPointerInputModifierNode {
        if (!enabled) {
            return@SuspendingPointerInputModifierNode
        }

        coroutineScope {
            launch(start = CoroutineStart.UNDISPATCHED) {
                while (isActive) {
                    var event = channel.receive()
                    if (event !is DragEvent.DragStarted) {
                        continue
                    }

                    onDragStarted(event.startPoint)

                    // Also catch Cancellation
                    try {
                        state.drag(MutatePriority.UserInput) {
                            while (event !is DragEvent.DragStopped && event !is DragEvent.DragCancelled) {
                                (event as? DragEvent.DragDelta)?.let { dragBy(it.delta.x) }
                                event = channel.receive()
                            }
                        }
                        event.let { ev ->
                            if (ev is DragEvent.DragStopped) {
                                onDragStopped(ev.velocity)
                            } else if (ev is DragEvent.DragCancelled) {
                                onDragStopped(Velocity.Zero)
                            }
                        }
                    } catch (ignored: Throwable) {
                        onDragStopped(Velocity.Zero)
                    }
                }
            }

            awaitEachGesture {
                val awaited = awaitDownAndSlop(
                    startDragImmediately = startDragImmediately,
                    velocityTracker = velocityTracker
                )

                if (awaited != null) {
                    var isDragSuccessful = false

                    // Also catch Cancellation
                    try {
                        isDragSuccessful = awaitDrag(
                            startEvent = awaited.first,
                            initialDelta = awaited.second,
                            velocityTracker = velocityTracker,
                            channel = channel,
                            reverseDirection = false
                        )
                    } catch (ignored: Throwable) {
                        isDragSuccessful = false
                        if (!isActive) {
                            throw ignored
                        }
                    } finally {
                        val event = if (isDragSuccessful) {
                            val velocity = velocityTracker.calculateVelocity()
                            velocityTracker.resetTracking()
                            DragEvent.DragStopped(velocity)
                        } else {
                            DragEvent.DragCancelled
                        }
                        channel.trySend(event)
                    }
                }
            }
        }
    }

    init {
        delegate(pointerInputNode)
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        pointerInputNode.onCancelPointerInput()
    }

    fun update(
        state: DraggableState,
        enabled: Boolean,
        startDragImmediately: () -> Boolean,
        onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit,
        onDragStopped: suspend CoroutineScope.(velocity: Velocity) -> Unit
    ) {
        var resetPointerInputHandling = false
        if (this.state != state) {
            this.state = state
            resetPointerInputHandling = true
        }
        if (this.enabled != enabled) {
            this.enabled = enabled
            resetPointerInputHandling = true
        }
        this.startDragImmediately = startDragImmediately
        this.onDragStarted = onDragStarted
        this.onDragStopped = onDragStopped

        if ( resetPointerInputHandling) {
            pointerInputNode.resetPointerInputHandler()
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitDownAndSlop(
    startDragImmediately: () -> Boolean,
    velocityTracker: VelocityTracker
): Pair<PointerInputChange, Offset>? {
    val initialDown = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
    return if (startDragImmediately()) {
        initialDown.consume()
        velocityTracker.addPointerInputChange(initialDown)

        initialDown to Offset.Zero
    } else {
        val down = awaitFirstDown(requireUnconsumed = false)
        velocityTracker.addPointerInputChange(down)

        var initialDelta = Offset.Zero
        val postPointerSlop = { event: PointerInputChange, overSlop: Float ->
            val isHorizontalSwipe = event.positionChange().let {
                abs(it.x) > abs(it.y * 2F)
            }
            if (isHorizontalSwipe) {
                velocityTracker.addPointerInputChange(event)
                event.consume()
                initialDelta = Offset(x = overSlop, 0F)
            } else {
                throw CancellationException()
            }
        }

        val afterSlopResult = awaitHorizontalTouchSlopOrCancellation(
            pointerId = down.id,
            onTouchSlopReached = postPointerSlop
        )

        if (afterSlopResult != null) {
            afterSlopResult to initialDelta
        } else {
            null
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitDrag(
    startEvent: PointerInputChange,
    initialDelta: Offset,
    velocityTracker: VelocityTracker,
    channel: SendChannel<DragEvent>,
    reverseDirection: Boolean
): Boolean {
    val xSign = sign(startEvent.position.x)
    val ySign = sign(startEvent.position.y)
    val adjustedStart = startEvent.position - Offset(initialDelta.x * xSign, initialDelta.y * ySign)

    channel.trySend(DragEvent.DragStarted(adjustedStart))
    channel.trySend(DragEvent.DragDelta(if (reverseDirection) initialDelta * -1F else initialDelta))

    return drag(pointerId = startEvent.id) { event ->
        velocityTracker.addPointerInputChange(event)

        if (!event.changedToUpIgnoreConsumed()) {
            val delta = event.positionChange()
            event.consume()
            channel.trySend(DragEvent.DragDelta(if (reverseDirection) delta * -1F else delta))
        }
    }
}

private sealed interface DragEvent {
    data class DragStarted(val startPoint: Offset): DragEvent
    data class DragStopped(val velocity: Velocity): DragEvent
    data object DragCancelled : DragEvent
    data class DragDelta(val delta: Offset): DragEvent
}