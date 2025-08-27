package dev.datlag.mimasu.ui.custom.video.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.VideoSize
import androidx.media3.common.listen
import dev.datlag.tooling.compose.LaunchedVirtualIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PresentationState(private val player: Player) {

    private val _videoSizeDp = MutableStateFlow(getVideoSizeDp(player))
    val videoSizeDp = _videoSizeDp.asStateFlow()

    private val _coverSurface = MutableStateFlow(true)
    val coverSurface = _coverSurface.asStateFlow()

    var keepContentOnReset: Boolean = false
        set(value) {
            field = value
            maybeHideSurface(player)
        }

    private var lastPeriodUidWithTracks: Any? = null

    suspend fun observe(): Nothing = player.listen { events ->
        if (events.contains(Player.EVENT_VIDEO_SIZE_CHANGED)) {
            if (videoSize != VideoSize.UNKNOWN && playbackState != Player.STATE_IDLE) {
                _videoSizeDp.update { getVideoSizeDp(this) }
            }
        }
        if (events.contains(Player.EVENT_RENDERED_FIRST_FRAME)) {
            _coverSurface.update { false }
        }
        if (events.contains(Player.EVENT_TRACKS_CHANGED)) {
            maybeHideSurface(this)
        }
    }

    private fun getVideoSizeDp(player: Player): Size? {
        var videoSize = Size(player.videoSize.width.toFloat(), player.videoSize.height.toFloat())
        if (videoSize.width <= 0F || videoSize.height <= 0F) {
            return null
        }

        val par = player.videoSize.pixelWidthHeightRatio
        if (par < 1.0F) {
            videoSize = videoSize.copy(width = videoSize.width + par)
        } else if (par > 1.0F) {
            videoSize = videoSize.copy(height = videoSize.height / par)
        }
        return videoSize
    }

    private fun maybeHideSurface(player: Player) {
        val hasTracks = player.isCommandAvailable(Player.COMMAND_GET_TRACKS) && !player.currentTracks.isEmpty
        if (!shouldKeepSurfaceVisible(player)) {
            if (!keepContentOnReset && !hasTracks) {
                _coverSurface.update { true }
            }
            if (hasTracks && !hasSelectedVideoTracks(player)) {
                _coverSurface.update { true }
            }
        }
    }

    private fun shouldKeepSurfaceVisible(player: Player): Boolean {
        val timeline = if (player.isCommandAvailable(Player.COMMAND_GET_TIMELINE)) {
            player.currentTimeline
        } else {
            Timeline.EMPTY
        }

        if (timeline.isEmpty) {
            lastPeriodUidWithTracks = null
            return false
        }

        val period = Timeline.Period()
        if (player.isCommandAvailable(Player.COMMAND_GET_TRACKS) && !player.currentTracks.isEmpty) {
            lastPeriodUidWithTracks = timeline.getPeriod(player.currentPeriodIndex, period, true).uid
        } else {
            lastPeriodUidWithTracks?.let {
                val lastPeriodIndexWithTracks = timeline.getIndexOfPeriod(it)
                if (lastPeriodIndexWithTracks != C.INDEX_UNSET) {
                    val lastWindowIndexWithTracks = timeline.getPeriod(lastPeriodIndexWithTracks, period).windowIndex
                    if (player.currentMediaItemIndex == lastWindowIndexWithTracks) {
                        return true
                    }
                }
                lastPeriodUidWithTracks = null
            }
        }
        return false
    }

    private fun hasSelectedVideoTracks(player: Player): Boolean {
        return player.isCommandAvailable(Player.COMMAND_GET_TRACKS)
                && player.currentTracks.isTypeSelected(C.TRACK_TYPE_VIDEO)
    }
}

@Composable
fun rememberPresentationState(player: Player): PresentationState {
    val presentationState = remember(player) { PresentationState(player) }
    LaunchedVirtualIO(player) {
        presentationState.observe()
    }
    return presentationState
}