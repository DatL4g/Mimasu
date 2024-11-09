package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.datlag.tooling.compose.withDefaultContext
import dev.datlag.tooling.compose.withIOContext
import dev.datlag.tooling.compose.withMainContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class VideoPlayerState internal constructor(
    private val hide: Duration = 2.seconds
) {

    private val _controlsVisibility = MutableStateFlow(true)

    val controlsVisibility = _controlsVisibility.asStateFlow()

    val controlsVisible: Boolean
        get() = controlsVisibility.value

    private val channel = Channel<Long>(CONFLATED)

    private val _contentPosition = MutableStateFlow(0L)
    val contentPosition = _contentPosition.asStateFlow()

    private val _contentDuration = MutableStateFlow(0L)
    val contentDuration = _contentDuration.asStateFlow()

    private val _wholePosition = MutableStateFlow(0L)
    val wholePosition = _wholePosition.asStateFlow()

    private val _wholeDuration = MutableStateFlow(0L)
    val wholeDuration = _wholeDuration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    fun showControls(duration: Duration = hide) {
        _controlsVisibility.update { true }
        channel.trySend(duration.inWholeSeconds)
    }

    fun hideControls() {
        _controlsVisibility.update { false }
        channel.trySend(1L)
    }

    fun toggleControls(duration: Duration = hide) {
        if (controlsVisible) {
            hideControls()
        } else {
            showControls(duration)
        }
    }

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow()
            .debounce { it * 1000 }
            .collect { _controlsVisibility.emit(false) }
    }

    internal fun updateContent(
        contentLength: Long,
        contentDuration: Long,

    ) {
        _contentPosition.update { contentLength }
        _contentDuration.update { contentDuration }
    }

    internal fun updateWhole(
        wholeLength: Long,
        wholeDuration: Long
    ) {
        _wholePosition.update { wholeLength }
        _wholeDuration.update { wholeDuration }
    }

    internal fun updatePlaying(value: Boolean) {
        _isPlaying.update { value }
    }

}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberVideoPlayerState(
    player: Player,
    hide: Duration = 2.seconds
): VideoPlayerState {
    val state = remember { VideoPlayerState(hide) }

    LaunchedEffect(player) {
        val wrapper = (player as? PlayerWrapper)

        wrapper?.listenProgress(object : PlayerWrapper.ProgressChange {
            override fun content(position: Long, duration: Long) {
                state.updateContent(position, duration)
            }

            override fun whole(position: Long, duration: Long) {
                state.updateWhole(position, duration)
            }
        })

        wrapper?.listenPlayback(object : PlayerWrapper.PlaybackChange {
            override fun playing() {
                state.updatePlaying(true)
            }

            override fun paused() {
                state.updatePlaying(false)
            }
        })
    }

    LaunchedEffect(state) {
        state.observe()
    }

    LaunchedEffect(player, state) {
        state.showControls()
        do {
            val (cLength, cDuration) = withMainContext {
                player.contentPosition to player.contentDuration
            }
            val (wLength, wDuration) = withMainContext {
                player.currentPosition to player.duration
            }

            withIOContext {
                state.updateContent(cLength, cDuration)
                state.updateWhole(wLength, wDuration)
            }
            withDefaultContext {
                delay(100)
            }
        } while (isActive)
    }

    return state
}