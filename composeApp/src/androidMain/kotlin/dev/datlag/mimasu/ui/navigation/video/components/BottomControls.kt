package dev.datlag.mimasu.ui.navigation.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.common.toDuration
import dev.datlag.mimasu.ui.navigation.video.states.ControlsState
import dev.datlag.mimasu.ui.navigation.video.states.ProgressState
import kotlin.math.roundToLong

@Composable
fun BottomControls(
    controlsState: ControlsState,
    state: ProgressState,
    modifier: Modifier = Modifier,
    pipActive: Boolean = PiPHelper.active.value,
) {
    val visibility by controlsState.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        modifier = modifier,
        visible = visibility && !pipActive,
        enter = slideInVertically { it / 2 } + fadeIn(),
        exit = slideOutVertically { it / 2 } + fadeOut()
    ) {
        BottomAppBar(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isDragging by interactionSource.collectIsDraggedAsState()
            val enabled by state.enabled.collectAsStateWithLifecycle()
            val position by state.contentPosition.collectAsStateWithLifecycle()
            val duration by state.contentDuration.collectAsStateWithLifecycle()
            var progress by remember { mutableFloatStateOf(0F) }
            val progressForText by remember(progress, duration) {
                derivedStateOf {
                    duration.times(progress).roundToLong()
                }
            }

            LaunchedEffect(position, duration) {
                if (!isDragging) {
                    progress = if (position <= 0L || duration <= 0L) {
                        0F
                    } else {
                        position.toFloat() / duration.toFloat()
                    }
                }
            }

            Text(
                text = progressForText.toDuration(),
                maxLines = 1
            )
            Slider(
                modifier = Modifier.padding(horizontal = 8.dp).weight(1F),
                value = progress,
                enabled = enabled,
                onValueChange = {
                    controlsState.showControls()

                    progress = it
                },
                onValueChangeFinished = {
                    state.seekTo(duration.times(progress).roundToLong())
                },
                interactionSource = interactionSource
            )
            Text(
                text = duration.toDuration(),
                maxLines = 1
            )
        }
    }
}