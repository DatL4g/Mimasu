package dev.datlag.mimasu.ui.navigation.video.components

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import dev.datlag.mimasu.common.mediumLargeContainerSize
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.custom.video.states.PlayPauseButtonState
import dev.datlag.mimasu.ui.custom.video.states.SeekState

@ExperimentalMaterial3ExpressiveApi
@OptIn(UnstableApi::class)
@Composable
fun CenterControls(
    controlsState: ControlsState,
    state: PlayPauseButtonState,
    seekState: SeekState,
    modifier: Modifier = Modifier,
    pipActive: Boolean = PiPHelper.active.value
) {
    val visibility by controlsState.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = visibility && !pipActive,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            val showPlay by state.showPlay.collectAsStateWithLifecycle()
            val enabled by state.isEnabled.collectAsStateWithLifecycle()
            val seekBackEnabled by seekState.seekBackEnabled.collectAsStateWithLifecycle()
            val seekForwardEnabled by seekState.seekForwardEnabled.collectAsStateWithLifecycle()

            IconButton(
                modifier = Modifier
                    .size(
                        IconButtonDefaults.mediumLargeContainerSize(
                            IconButtonDefaults.IconButtonWidthOption.Narrow
                        )
                    ),
                onClick = {
                    seekState.seekBack()
                    controlsState.showControls()
                },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledTonalIconButtonColors(),
                enabled = seekBackEnabled
            ) {
                MaterialSymbols(
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
                    name = MaterialSymbols.REPLAY,
                    contentDescription = null
                )
            }

            IconButton(
                modifier = Modifier
                    .size(
                        IconButtonDefaults.mediumLargeContainerSize(
                            IconButtonDefaults.IconButtonWidthOption.Wide
                        )
                    ),
                onClick = {
                    state.onClick()
                },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors(),
                enabled = enabled
            ) {
                MaterialSymbols(
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
                    name = if (showPlay) {
                        MaterialSymbols.PLAY_ARROW
                    } else {
                        MaterialSymbols.PAUSE
                    },
                    contentDescription = null,
                    filled = true
                )
            }

            IconButton(
                modifier = Modifier
                    .size(
                        IconButtonDefaults.mediumLargeContainerSize(
                            IconButtonDefaults.IconButtonWidthOption.Narrow
                        )
                    ),
                onClick = {
                    seekState.seekForward()
                    controlsState.showControls()
                },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledTonalIconButtonColors(),
                enabled = seekForwardEnabled
            ) {
                MaterialSymbols(
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
                    name = MaterialSymbols.FORWARD_MEDIA,
                    contentDescription = null
                )
            }
        }
    }
}