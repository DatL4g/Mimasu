package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import dev.datlag.mimasu.common.drawProgress
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import ir.mahozad.multiplatform.wavyslider.material3.WavySlider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max
import kotlin.math.roundToLong
import androidx.compose.ui.tooling.preview.Preview as AndroidPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerControls(
    state: VideoPlayerState,
    modifier: Modifier = Modifier,
    onRewind: () -> Unit,
    onPlayPause: () -> Unit,
    onForward: () -> Unit,
    onSeekFinished: (Long) -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (rewind, playPause, forward, seeker) = createRefs()
        val visibility by state.controlsVisibility.collectAsStateWithLifecycle()

        AnimatedVisibility(
            modifier = Modifier.constrainAs(rewind) {
                start.linkTo(parent.start)
                end.linkTo(playPause.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 8.dp)
            },
            visible = visibility,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {
            IconButton(
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.25F),
                    shape = CircleShape
                ),
                onClick = {
                    state.showControls()

                    onRewind()
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.FastRewind,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier.constrainAs(playPause) {
                start.linkTo(rewind.end)
                end.linkTo(forward.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 8.dp)
            },
            visible = visibility,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton(
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.25F),
                    shape = CircleShape
                ),
                onClick = {
                    state.showControls()

                    onPlayPause()
                }
            ) {
                val isPlaying by state.isPlaying.collectAsStateWithLifecycle()

                Icon(
                    imageVector = if (isPlaying) {
                        Icons.Rounded.Pause
                    } else {
                        Icons.Rounded.PlayArrow
                    },
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier.constrainAs(forward) {
                start.linkTo(playPause.end)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 8.dp)
            },
            visible = visibility,
            enter = slideInHorizontally { it / 2 } + fadeIn(),
            exit = slideOutHorizontally { it / 2 } + fadeOut()
        ) {
            IconButton(
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.25F),
                    shape = CircleShape
                ),
                onClick = {
                    state.showControls()

                    onForward()
                }
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawProgress(
                            color = Color.White,
                            progress = 0F
                        )
                        .padding(8.dp),
                    imageVector = Icons.Rounded.FastForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isDragging by interactionSource.collectIsDraggedAsState()
        val position by state.contentPosition.collectAsStateWithLifecycle()
        val duration by state.contentDuration.collectAsStateWithLifecycle()
        var progress by remember { mutableFloatStateOf(0F) }

        LaunchedEffect(position, duration) {
            if (!isDragging) {
                progress = if (position <= 0L || duration <= 0L) {
                    0F
                } else {
                    position.toFloat() / duration.toFloat()
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(seeker) {
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    bottom.linkTo(parent.bottom, 8.dp)
                },
            visible = visibility,
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = slideOutVertically { it / 2 } + fadeOut()
        ) {
            WavySlider(
                modifier = Modifier.fillMaxWidth(),
                value = progress,
                onValueChange = {
                    state.showControls()

                    progress = it
                },
                onValueChangeFinished = {
                    onSeekFinished(duration.times(progress).roundToLong())
                },
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
        }
    }
}

@AndroidPreview
@Preview
@Composable
private fun PlayerControlsPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        PlayerControls(
            state = VideoPlayerState(),
            modifier = Modifier.fillMaxSize(),
            onRewind = { },
            onPlayPause = { },
            onForward = { },
            onSeekFinished = { }
        )
    }
}