package dev.datlag.mimasu.ui.navigation.screen.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.common.toDuration
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.navigation.screen.video.VideoPlayerState
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import ir.mahozad.multiplatform.wavyslider.material3.WaveHeight
import ir.mahozad.multiplatform.wavyslider.material3.WavySlider
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomControls(
    state: VideoPlayerState,
    pipActive: Boolean = PiPHelper.active.value
) {
    val visibility by state.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(),
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
            val adInfo by state.adInfo.collectAsStateWithLifecycle()
            val seekable by state.isSeekable.collectAsStateWithLifecycle()
            val position by state.contentPosition.collectAsStateWithLifecycle()
            val duration by state.contentDuration.collectAsStateWithLifecycle()
            val isPlaying by state.isPlaying.collectAsStateWithLifecycle()
            var progress by remember { mutableFloatStateOf(0F) }
            val progressForText by remember(progress, duration) {
                derivedStateOf {
                    duration.times(progress).roundToLong()
                }
            }
            val animatedHeight by animateDpAsState(
                targetValue = if (isPlaying) {
                    SliderDefaults.WaveHeight
                } else {
                    0.dp
                }
            )

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

            WavySlider(
                modifier = Modifier.padding(horizontal = 8.dp).weight(1F),
                value = progress,
                enabled = !adInfo.playing && seekable,
                onValueChange = {
                    state.showControls()

                    progress = it
                },
                onValueChangeFinished = {
                    state.seekTo(
                        positionMs = duration.times(progress).roundToLong()
                    )
                },
                waveHeight = animatedHeight,
                incremental = true,
                trackThickness = 8.dp,
                interactionSource = interactionSource,
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        thumbSize = DpSize(
                            width = 4.dp,
                            height = 22.dp
                        )
                    )
                }
            )

            Text(
                text = duration.toDuration(),
                maxLines = 1
            )
        }
    }
}