package dev.datlag.mimasu.ui.navigation.screen.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeMute
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.other.AudioHelper
import dev.datlag.mimasu.other.BrightnessHelper
import dev.datlag.mimasu.ui.navigation.screen.video.VideoPlayerState
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VolumeBrightnessControl(
    state: VideoPlayerState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        val audioHelper = remember(context) { AudioHelper(context) }
        val brightnessHelper = remember(context) { BrightnessHelper(context) }

        var volumeVisible by remember { mutableStateOf(false) }
        var volumeProgress by remember { mutableFloatStateOf(audioHelper.volumeProgress) }

        var brightnessVisible by remember { mutableStateOf(false) }
        var brightnessProgress by remember { mutableFloatStateOf(brightnessHelper.brightness) }

        FlowRow(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = contentPadding.calculateTopPadding()),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            AnimatedVisibility(
                visible = volumeVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .background(color = Color.Black.copy(alpha = 0.25F), CircleShape)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    Icon(
                        imageVector = volumeProgress.mapToVolume(),
                        contentDescription = null,
                        tint = Color.White
                    )

                    HorizontalProgress(
                        modifier = Modifier.height(8.dp).width(120.dp),
                        progress = volumeProgress
                    )

                    Text(
                        text = (volumeProgress * 100F).roundToInt().toString(),
                        maxLines = 1,
                        color = Color.White
                    )
                }
            }

            AnimatedVisibility(
                visible = brightnessVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .background(color = Color.Black.copy(alpha = 0.25F), CircleShape)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LightMode,
                        contentDescription = null,
                        tint = Color.White
                    )

                    HorizontalProgress(
                        modifier = Modifier.height(8.dp).width(120.dp),
                        progress = max(brightnessProgress, 0F)
                    )

                    Text(
                        text = if (brightnessProgress < brightnessHelper.minBrightness) {
                            "Auto"
                        } else {
                            (brightnessProgress * 100F).roundToInt().toString()
                        },
                        maxLines = 1,
                        color = Color.White
                    )
                }
            }
        }

        Row(
            modifier = modifier
        ) {
            var widthLeft by remember { mutableIntStateOf(0) }
            var widthRight by remember { mutableIntStateOf(0) }
            val canSeekBack by state.canSeekBack.collectAsStateWithLifecycle()
            val canSeekForward by state.canSeekForward.collectAsStateWithLifecycle()

            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
                    .onSizeChanged {
                        widthLeft = it.width
                    }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                brightnessVisible = true
                            },
                            onDragEnd = {
                                brightnessVisible = false
                            }
                        ) { _, dragAmount ->
                            brightnessProgress = (brightnessProgress + -dragAmount / 1000F).coerceIn(brightnessHelper.minBrightness - 0.01F, brightnessHelper.maxBrightness)

                            brightnessHelper.brightness = brightnessProgress
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                val center = widthLeft.toFloat() / 2F

                                if (it.x < center && canSeekBack) {
                                    state.seekBack()
                                }
                            },
                            onTap = {
                                state.toggleControls()
                            }
                        )
                    }
                    .padding(8.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
                    .onSizeChanged {
                        widthRight = it.width
                    }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                volumeVisible = true
                            },
                            onDragEnd = {
                                volumeVisible = false
                            }
                        ) { _, dragAmount ->
                            volumeProgress = (volumeProgress + -dragAmount / 1000F).coerceIn(0F, 1F)

                            audioHelper.volumeProgress = volumeProgress
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                val center = widthRight.toFloat() / 2F

                                if (it.x > center && canSeekForward) {
                                    state.seekForward()
                                }
                            },
                            onTap = {
                                state.toggleControls()
                            }
                        )
                    }
                    .padding(8.dp)
            )

            DisposableEffect(audioHelper) {
                onDispose {
                    audioHelper.dispose()
                    brightnessHelper.dispose()
                }
            }
        }
    }
}

@Composable
private fun HorizontalProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    backgroundColor: Color = Color.Black.copy(alpha = 0.5F),
) {
    Canvas(
        modifier = modifier
            .clip(CircleShape)
    ) {
        // Progress made
        drawRect(
            color = color,
            size = Size((progress * size.width), height = size.height),
        )
        // background
        drawRect(
            color = backgroundColor,
            size = Size(
                width = (1 - progress) * size.width,
                height = size.height
            ),
            topLeft = Offset(
                x = progress * size.width,
                y = 0F
            )
        )
    }
}

private fun Float.mapToVolume(): ImageVector {
    return when {
        this >= 0.6F -> Icons.AutoMirrored.Rounded.VolumeUp
        this >= 0.2F -> Icons.AutoMirrored.Rounded.VolumeDown
        this <= 0.01F -> Icons.AutoMirrored.Rounded.VolumeOff
        else -> Icons.AutoMirrored.Rounded.VolumeMute
    }
}