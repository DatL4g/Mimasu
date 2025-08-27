package dev.datlag.mimasu.ui.custom.video.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.datlag.tooling.compose.LaunchedVirtualIO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ControlsState(
    private val hide: Duration = 2.seconds
) {

    private val _controlsVisibility = MutableStateFlow(true)
    val controlsVisibility = _controlsVisibility.asStateFlow()
    val controlsVisible: Boolean
        get() = controlsVisibility.value

    private val controlsChannel = Channel<Long>(CONFLATED)

    init {
        controlsChannel.sendSafely(hide.inWholeSeconds)
    }

    fun showControls(duration: Duration = hide) {
        _controlsVisibility.update { true }
        controlsChannel.sendSafely(duration.inWholeSeconds)
    }

    fun hideControls() {
        _controlsVisibility.update { false }
        controlsChannel.sendSafely(1L)
    }

    fun toggleControls(duration: Duration = hide) {
        if (controlsVisible) {
            hideControls()
        } else {
            showControls(duration)
        }
    }

    suspend fun observe() {
        controlsChannel.consumeAsFlow()
            .debounce { it * 1000 }
            .collect { _controlsVisibility.emit(false) }
    }

    private fun <T> Channel<T>.sendSafely(value: T) {
        this.trySend(value).onFailure {
            this.trySendBlocking(value)
        }
    }
}

@Composable
fun rememberControlsState(duration: Duration = 2.seconds): ControlsState {
    val controlsState = remember(duration) { ControlsState(duration) }
    LaunchedVirtualIO(duration) {
        controlsState.observe()
    }
    return controlsState
}