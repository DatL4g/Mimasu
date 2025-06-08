package dev.datlag.mimasu.ui.navigation.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.mimasu.ui.navigation.video.states.ControlsState
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes

@Composable
fun ExtraControls(
    controlsState: ControlsState,
    hazeState: HazeState,
    player: Player,
    viewModel: VideoViewModel,
    modifier: Modifier = Modifier
) {
    val surface = Platform.colorScheme().surface
    val onSurface = Platform.colorScheme().onSurface

    val visibility by controlsState.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = visibility,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier
                .clip(Platform.shapes().medium)
                .hazeEffect(
                    state = hazeState,
                    style = HazeMaterials.thin(surface)
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaybackSpeedButton(
                controlsState = controlsState,
                player = player,
                color = onSurface
            )
            SourceButton(
                controlsState = controlsState,
                viewModel = viewModel,
                color = onSurface
            )
        }
    }
}