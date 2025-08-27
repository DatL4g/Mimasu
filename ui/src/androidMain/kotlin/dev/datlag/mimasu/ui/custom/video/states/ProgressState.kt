package dev.datlag.mimasu.ui.custom.video.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.media3.common.Player
import dev.datlag.tooling.async.withDefaultContext
import dev.datlag.tooling.async.withMainContext
import dev.datlag.tooling.compose.LaunchedVirtualIO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ProgressState(private val player: Player) {

    private val _enabled = MutableStateFlow(getSeekingAvailable())
    val enabled = _enabled.asStateFlow()

    private val _contentPosition = MutableStateFlow(getContentPosition())
    val contentPosition = _contentPosition.asStateFlow()

    private val _contentDuration = MutableStateFlow(getContentDuration())
    val contentDuration = _contentDuration.asStateFlow()

    private val _contentBufferedPosition = MutableStateFlow(getContentBufferedPosition())
    val contentBufferedPosition = _contentBufferedPosition

    private val _position = MutableStateFlow(getPosition())
    val position = _position.asStateFlow()

    private val _duration = MutableStateFlow(getDuration())
    val duration = _duration.asStateFlow()

    private val _bufferedPosition = MutableStateFlow(getBufferedPosition())
    val bufferedPosition = _bufferedPosition.asStateFlow()

    suspend fun observe(rate: Duration = 100.milliseconds) = withDefaultContext {
        do {
            update()

            delay(max(rate.inWholeMilliseconds, 100))
        } while (isActive)
    }

    fun seekTo(position: Long) {
        if (player.isCommandAvailable(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)) {
            player.seekTo(position)
        }
    }

    private suspend fun update() = withDefaultContext {
        _contentPosition.emit(withMainContext { getContentPosition() })
        _contentDuration.emit(withMainContext { getContentDuration() })
        _contentBufferedPosition.emit(withMainContext { getContentBufferedPosition() })

        _position.emit(withMainContext { getPosition() })
        _duration.emit(withMainContext { getDuration() })
        _bufferedPosition.emit(withMainContext { getBufferedPosition() })

        _enabled.emit(withMainContext { getSeekingAvailable() })
    }

    private fun getContentPosition(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.contentPosition
        } else {
            0L
        }
    }

    private fun getContentDuration(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.contentDuration
        } else {
            0L
        }
    }

    private fun getContentBufferedPosition(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.contentBufferedPosition
        } else {
            0L
        }
    }

    private fun getPosition(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.currentPosition
        } else {
            0L
        }
    }

    private fun getDuration(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.duration
        } else {
            0L
        }
    }

    private fun getBufferedPosition(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.bufferedPosition
        } else {
            0L
        }
    }

    private fun getSeekingAvailable(): Boolean {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.isCurrentMediaItemSeekable
        } else {
            player.isCommandAvailable(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
        }
    }
}

@Composable
fun rememberProgressState(player: Player): ProgressState {
    val progressState = remember(player) { ProgressState(player) }
    LaunchedVirtualIO(player) { progressState.observe() }
    return progressState
}