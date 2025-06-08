package dev.datlag.mimasu.ui.navigation.video.components

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.video.states.ControlsState
import dev.datlag.mimasu.ui.navigation.video.states.PlayPauseButtonState
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes

@OptIn(UnstableApi::class)
@Composable
fun CenterControls(
    controlsState: ControlsState,
    state: PlayPauseButtonState,
    hazeState: HazeState,
    modifier: Modifier = Modifier
) {
    val visibility by controlsState.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = visibility,
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
            val surface = Platform.colorScheme().surface
            val onSurface = Platform.colorScheme().onSurface

            IconButton(
                modifier = Modifier
                    .clip(Platform.shapes().medium)
                    .hazeEffect(
                        state = hazeState,
                        style = HazeMaterials.regular(surface)
                    )
                    .padding(8.dp),
                onClick = {
                    state.onClick()
                },
                enabled = enabled
            ) {
                MaterialSymbols(
                    name = if (showPlay) {
                        MaterialSymbols.PLAY_ARROW
                    } else {
                        MaterialSymbols.PAUSE
                    },
                    contentDescription = null,
                    filled = true,
                    tint = onSurface
                )
            }
        }
    }
}