package dev.datlag.mimasu.ui.navigation.video.components

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import dev.datlag.mimasu.other.AudioHelper
import dev.datlag.mimasu.other.rememberBrightnessHelper
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.navigation.video.VideoLayout
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.contentColorFor
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(UnstableApi::class)
@Composable
fun VolumeBrightnessControl(
    controlsState: ControlsState,
    layout: VideoLayout,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        val audioHelper = remember(context) { AudioHelper(context) }
        val brightnessHelper = rememberBrightnessHelper()

        var volumeVisible by remember { mutableStateOf(false) }
        var volumeProgress by remember { mutableFloatStateOf(audioHelper.volumeProgress) }

        var brightnessVisible by remember { mutableStateOf(false) }
        var brightnessProgress by remember { mutableFloatStateOf(brightnessHelper.brightness) }

        val surface = Platform.colorScheme().surface
        val onSurface = Platform.contentColorFor(surface)

        FlowRow(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = contentPadding.calculateTopPadding()),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            AnimatedVisibility(
                visible = volumeVisible && layout !is VideoLayout.Portrait,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(surface, CircleShape)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    MaterialSymbols(
                        name = volumeProgress.mapToVolume(),
                        contentDescription = null,
                        tint = onSurface,
                        filled = true
                    )

                    HorizontalProgress(
                        modifier = Modifier.height(8.dp).width(120.dp),
                        progress = volumeProgress,
                        color = onSurface,
                        backgroundColor = surface.copy(alpha = 0.5F)
                    )

                    Text(
                        text = (volumeProgress * 100F).roundToInt().toString(),
                        maxLines = 1,
                        color = onSurface
                    )
                }
            }

            AnimatedVisibility(
                visible = brightnessVisible && layout !is VideoLayout.Portrait,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(surface, CircleShape)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.LIGHT_MODE,
                        contentDescription = null,
                        tint = onSurface,
                        filled = true
                    )

                    HorizontalProgress(
                        modifier = Modifier.height(8.dp).width(120.dp),
                        progress = max(brightnessProgress, 0F),
                        color = onSurface,
                        backgroundColor = surface.copy(alpha = 0.5F)
                    )

                    Text(
                        text = if (brightnessProgress < brightnessHelper.minBrightness) {
                            "Auto"
                        } else {
                            (brightnessProgress * 100F).roundToInt().toString()
                        },
                        maxLines = 1,
                        color = onSurface
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures {
                    controlsState.toggleControls()
                }
            }
        ) {
            val (start, end) = if (LocalLayoutDirection.current == LayoutDirection.Ltr) {
                0.dp to 24.dp
            } else {
                24.dp to 0.dp
            }

            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
                    .padding(start = start, end = end)
                    .ifFalse(layout is VideoLayout.Portrait) {
                        pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    brightnessVisible = true
                                },
                                onDragEnd = {
                                    brightnessVisible = false
                                },
                                onVerticalDrag = { _, dragAmount ->
                                    brightnessProgress = (brightnessProgress + -dragAmount / 1000F).coerceIn(brightnessHelper.minBrightness - 0.01F, brightnessHelper.maxBrightness)
                                    brightnessHelper.brightness = brightnessProgress
                                }
                            )
                        }
                    }
                    .padding(8.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
                    .padding(start = end, end = start)
                    .ifFalse(layout is VideoLayout.Portrait) {
                        pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    volumeVisible = true
                                },
                                onDragEnd = {
                                    volumeVisible = false
                                },
                                onVerticalDrag = { _, dragAmount ->
                                    volumeProgress = (volumeProgress + -dragAmount / 1000F).coerceIn(0F, 1F)

                                    audioHelper.volumeProgress = volumeProgress
                                }
                            )
                        }
                    }
                    .padding(8.dp)
            )
        }

        DisposableEffect(audioHelper) {
            onDispose {
                audioHelper.dispose()
            }
        }
        DisposableEffect(brightnessHelper) {
            onDispose {
                brightnessHelper.dispose()
            }
        }
    }
}

@Composable
private fun HorizontalProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color,
    backgroundColor: Color
) {
    Canvas(
        modifier = modifier.clip(CircleShape)
    ) {
        // Progress made
        drawRect(
            color = color,
            size = Size((progress * size.width), size.height)
        )

        // Background
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

private fun Float.mapToVolume(): String {
    return when {
        this >= 0.6F -> MaterialSymbols.VOLUME_UP
        this >= 0.2F -> MaterialSymbols.VOLUME_DOWN
        this <= 0.01F -> MaterialSymbols.VOLUME_OFF
        else -> MaterialSymbols.VOLUME_MUTE
    }
}