package dev.datlag.mimasu.ui.custom.video.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.media3.common.Player
import androidx.media3.common.listen
import dev.datlag.tooling.compose.LaunchedVirtualIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SeekState(private val player: Player) {

    private val _seekBackEnabled = MutableStateFlow(false)
    val seekBackEnabled = _seekBackEnabled.asStateFlow()

    private val _seekForwardEnabled = MutableStateFlow(false)
    val seekForwardEnabled = _seekForwardEnabled.asStateFlow()

    fun seekBack() {
        if (canSeekBack(player)) {
            player.seekBack()
        }
    }

    fun seekForward() {
        if (canSeekForward(player)) {
            player.seekForward()
        }
    }

    suspend fun observe(): Nothing = player.listen { events ->
        if (events.containsAny(
                Player.EVENT_PLAYBACK_STATE_CHANGED,
                Player.EVENT_PLAY_WHEN_READY_CHANGED,
                Player.EVENT_AVAILABLE_COMMANDS_CHANGED
        )) {
            _seekBackEnabled.update { canSeekBack(this) }
            _seekForwardEnabled.update { canSeekForward(this) }
        }
    }

    private fun canSeekBack(player: Player): Boolean {
        return player.isCommandAvailable(Player.COMMAND_SEEK_BACK)
    }

    private fun canSeekForward(player: Player): Boolean {
        return player.isCommandAvailable(Player.COMMAND_SEEK_FORWARD)
    }
}

@Composable
fun rememberSeekState(player: Player): SeekState {
    val seekState = remember(player) { SeekState(player) }

    LaunchedVirtualIO(player) {
        seekState.observe()
    }

    return seekState
}