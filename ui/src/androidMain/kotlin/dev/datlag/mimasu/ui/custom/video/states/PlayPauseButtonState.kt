package dev.datlag.mimasu.ui.custom.video.states

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.media3.common.Player
import androidx.media3.common.listen
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import dev.datlag.tooling.compose.LaunchedVirtualIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@UnstableApi
class PlayPauseButtonState(private val player: Player) {

    private val _isEnabled = MutableStateFlow(Util.shouldEnablePlayPauseButton(player))
    val isEnabled = _isEnabled.asStateFlow()

    private val _showPlay = MutableStateFlow(Util.shouldShowPlayButton(player))
    val showPlay = _showPlay.asStateFlow()

    fun onClick() {
        Util.handlePlayPauseButtonAction(player)
    }

    fun play() {
        if (isEnabled.value) {
            player.play()
        }
    }

    fun pause() {
        if (isEnabled.value) {
            player.pause()
        }
    }

    suspend fun observe(): Nothing = player.listen { events ->
        if (events.containsAny(
                Player.EVENT_PLAYBACK_STATE_CHANGED,
                Player.EVENT_PLAY_WHEN_READY_CHANGED,
                Player.EVENT_AVAILABLE_COMMANDS_CHANGED
        )) {
            _showPlay.update { Util.shouldShowPlayButton(this) }
            _isEnabled.update { Util.shouldEnablePlayPauseButton(this) }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun rememberPlayPauseButtonState(player: Player): PlayPauseButtonState {
    val playPauseButtonState = remember(player) { PlayPauseButtonState(player) }
    LaunchedVirtualIO(player) {
        playPauseButtonState.observe()
    }
    return playPauseButtonState
}