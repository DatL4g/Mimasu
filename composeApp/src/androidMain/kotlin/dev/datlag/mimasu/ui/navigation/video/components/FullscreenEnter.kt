package dev.datlag.mimasu.ui.navigation.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.video.states.ControlsState

@Composable
fun FullscreenEnter(
    controlsState: ControlsState,
    modifier: Modifier = Modifier,
    onEnter: () -> Unit
) {
    val visibility by controlsState.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = visibility,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        IconButton(
            onClick = onEnter
        ) {
            MaterialSymbols(
                name = MaterialSymbols.FULLSCREEN,
                contentDescription = null
            )
        }
    }
}