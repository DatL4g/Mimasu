package dev.datlag.mimasu.tv.ui.navigation.video

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.video.ProgressBar
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.custom.video.states.PlayPauseButtonState
import dev.datlag.mimasu.ui.custom.video.states.ProgressState
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel

@OptIn(UnstableApi::class)
@Composable
internal fun PlayerControls(
    playPauseButtonState: PlayPauseButtonState,
    progressState: ProgressState,
    controlsState: ControlsState,
    watchType: VideoViewModel.WatchType?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        val isVisible by controlsState.controlsVisibility.collectAsState()
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(isVisible) {
            if (isVisible) {
                focusRequester.requestFocus()
            }
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
            visible = isVisible,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = watchType?.title ?: "",
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
            visible = isVisible,
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = slideOutVertically { it / 2 } + fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                val playPauseEnabled by playPauseButtonState.isEnabled.collectAsState()
                val showPlay by playPauseButtonState.showPlay.collectAsState()

                IconButton(
                    modifier = Modifier.focusRequester(focusRequester),
                    onClick = {
                        playPauseButtonState.onClick()
                    },
                    enabled = playPauseEnabled
                ) {
                    if (showPlay) {
                        MaterialSymbols(
                            name = MaterialSymbols.PLAY_ARROW,
                            contentDescription = null,
                            filled = true
                        )
                    } else {
                        MaterialSymbols(
                            name = MaterialSymbols.PAUSE,
                            contentDescription = null,
                            filled = true
                        )
                    }
                }

                ProgressBar(
                    controlsState = controlsState,
                    state = progressState
                )
            }
        }
    }
}