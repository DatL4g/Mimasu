package dev.datlag.mimasu.ui.custom.video

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.common.toDuration
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.custom.video.states.ProgressState
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.LaunchedDefault
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import kotlin.math.roundToLong

@Composable
fun RowScope.ProgressBar(
    controlsState : ControlsState,
    state: ProgressState
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()
    val enabled by state.enabled.collectAsStateWithLifecycle()
    val position by state.position.collectAsStateWithLifecycle()
    val duration by state.duration.collectAsStateWithLifecycle()
    var progress by remember { mutableFloatStateOf(0F) }
    val progressForText by remember(progress, duration) {
        derivedStateOf {
            duration.times(progress).roundToLong()
        }
    }

    LaunchedDefault(position, duration) {
        if (!isDragging) {
            progress = if (position <= 0L || duration <= 0L) {
                0F
            } else {
                position.toFloat() / duration.toFloat()
            }
        }
    }

    PlatformText(
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
        interactionSource = interactionSource,
        colors = SliderDefaults.colors(
            thumbColor = Platform.colorScheme().primary,
            activeTrackColor = Platform.colorScheme().primary,
            activeTickColor = Platform.colorScheme().secondaryContainer,
            inactiveTrackColor = Platform.colorScheme().secondaryContainer,
            inactiveTickColor = Platform.colorScheme().primary,
            disabledThumbColor = Platform.colorScheme().onSurface
                .copy(alpha = 0.38F)
                .compositeOver(Platform.colorScheme().surface),
            disabledActiveTrackColor = Platform.colorScheme().onSurface.copy(alpha = 0.38F),
            disabledActiveTickColor = Platform.colorScheme().onSurface.copy(alpha = 0.12F),
            disabledInactiveTrackColor = Platform.colorScheme().onSurface.copy(alpha = 0.12F),
            disabledInactiveTickColor = Platform.colorScheme().onSurface.copy(alpha = 0.38F)
        )
    )
    PlatformText(
        text = duration.toDuration(),
        maxLines = 1
    )
}