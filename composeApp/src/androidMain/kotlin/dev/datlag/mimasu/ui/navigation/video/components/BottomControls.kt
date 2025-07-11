package dev.datlag.mimasu.ui.navigation.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.custom.video.ProgressBar
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.custom.video.states.ProgressState

@Composable
fun BottomControls(
    controlsState: ControlsState,
    isInCompactMode: Boolean,
    state: ProgressState,
    modifier: Modifier = Modifier,
    pipActive: Boolean = PiPHelper.active.value,
) {
    val visibility by controlsState.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        modifier = modifier,
        visible = visibility && !pipActive && !isInCompactMode,
        enter = slideInVertically { it / 2 } + fadeIn(),
        exit = slideOutVertically { it / 2 } + fadeOut()
    ) {
        BottomAppBar(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ) {
            ProgressBar(
                controlsState = controlsState,
                state = state
            )
        }
    }
}