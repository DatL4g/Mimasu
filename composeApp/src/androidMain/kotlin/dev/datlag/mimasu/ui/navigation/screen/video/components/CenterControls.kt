package dev.datlag.mimasu.ui.navigation.screen.video.components

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import dev.datlag.mimasu.common.drawProgress
import dev.datlag.mimasu.ui.custom.WindowSize
import dev.datlag.mimasu.ui.custom.calculateWindowWidthSize
import dev.datlag.mimasu.ui.navigation.screen.video.VideoPlayerState
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import ir.mahozad.multiplatform.wavyslider.material3.WavySlider
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterControls(
    state: VideoPlayerState,
    shownInDialog: Boolean,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (rewind, playPause, forward) = createRefs()
        val visibility by state.controlsVisibility.collectAsStateWithLifecycle()
        val showSeekButtons = if (shownInDialog) {
            calculateWindowWidthSize() !is WindowSize.Compact
        } else {
            true
        }

        AnimatedVisibility(
            modifier = Modifier.constrainAs(rewind) {
                start.linkTo(parent.start)
                end.linkTo(playPause.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 8.dp)
            },
            visible = visibility && showSeekButtons,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {
            val canSeekBack by state.canSeekBack.collectAsStateWithLifecycle()

            IconButton(
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.25F),
                    shape = CircleShape
                ),
                onClick = {
                    state.seekBack()
                },
                enabled = canSeekBack
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
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 8.dp)
            },
            visible = visibility,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                val isLoading by state.isLoading.collectAsStateWithLifecycle()
                val canPlayPause by state.canPlayPause.collectAsStateWithLifecycle()

                IconButton(
                    modifier = Modifier.background(
                        color = Color.Black.copy(alpha = 0.25F),
                        shape = CircleShape
                    ),
                    onClick = {
                        state.togglePlayPause()
                    },
                    enabled = canPlayPause
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

                AnimatedVisibility(
                    modifier = Modifier.matchParentSize(),
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.constrainAs(forward) {
                start.linkTo(playPause.end)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 8.dp)
            },
            visible = visibility && showSeekButtons,
            enter = slideInHorizontally { it / 2 } + fadeIn(),
            exit = slideOutHorizontally { it / 2 } + fadeOut()
        ) {
            val canSeekForward by state.canSeekForward.collectAsStateWithLifecycle()

            IconButton(
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.25F),
                    shape = CircleShape
                ),
                onClick = {
                    state.seekForward()
                },
                enabled = canSeekForward
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


    }
}
