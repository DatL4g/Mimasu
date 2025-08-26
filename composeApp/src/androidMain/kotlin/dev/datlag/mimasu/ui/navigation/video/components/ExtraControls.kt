package dev.datlag.mimasu.ui.navigation.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.navigation.video.VideoLayout
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme

@Composable
fun ExtraControls(
    controlsState: ControlsState,
    layout: VideoLayout,
    player: Player,
    viewModel: VideoViewModel,
    pipHelper: PiPHelper,
    modifier: Modifier = Modifier,
    pipActive: Boolean = PiPHelper.active.value,
    enterPiP: () -> Unit
) {
    val surface = FloatingActionButtonDefaults.containerColor
    val onSurface = Platform.colorScheme().contentColorFor(surface)
    val shape = FloatingActionButtonDefaults.extendedFabShape

    val visibility by controlsState.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = (visibility || layout is VideoLayout.Portrait) && !pipActive,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier
                .clip(shape)
                .background(surface, shape)
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
            PiPButton(
                pipHelper = pipHelper,
                color = onSurface,
                enterPiP = enterPiP
            )
        }
    }
}