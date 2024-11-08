package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeMute
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.other.AudioHelper
import dev.datlag.mimasu.other.BrightnessHelper

@Composable
fun VolumeBrightnessControl(
    modifier: Modifier = Modifier,
    onDoubleClickLeft: (Offset) -> Unit = { },
    onDoubleClickRight: (Offset) -> Unit = { },
    onTap: (Offset) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        val context = LocalContext.current
        val audioHelper = remember(context) { AudioHelper(context) }
        val brightnessHelper = remember(context) { BrightnessHelper(context) }

        var volumeVisible by remember { mutableStateOf(false) }
        var volumeProgress by remember { mutableFloatStateOf(audioHelper.volumeProgress) }

        var brightnessVisible by remember { mutableStateOf(false) }
        var brightnessProgress by remember { mutableFloatStateOf(brightnessHelper.brightness) }

        Column(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            brightnessVisible = true
                        },
                        onDragEnd = {
                            brightnessVisible = false
                        }
                    ) { _, dragAmount ->
                        brightnessProgress = (brightnessProgress + -dragAmount / 1000F).coerceIn(0F, 1F)

                        brightnessHelper.brightness = brightnessProgress
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = onDoubleClickLeft,
                        onTap = onTap
                    )
                }
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start
        ) {
            AnimatedVisibility(
                modifier = Modifier.safeDrawingPadding(),
                visible = volumeVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .background(color = Color.Black.copy(alpha = 0.25F), CircleShape)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = volumeProgress.mapToVolume(),
                        contentDescription = null,
                        tint = Color.White
                    )

                    VerticalProgress(
                        modifier = Modifier.height(120.dp).width(8.dp),
                        progress = volumeProgress
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
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
                        onDoubleTap = onDoubleClickRight,
                        onTap = onTap
                    )
                }
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.End
        ) {
            AnimatedVisibility(
                modifier = Modifier.safeDrawingPadding(),
                visible = brightnessVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .background(color = Color.Black.copy(alpha = 0.25F), CircleShape)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LightMode,
                        contentDescription = null,
                        tint = Color.White
                    )

                    VerticalProgress(
                        modifier = Modifier.height(120.dp).width(8.dp),
                        progress = brightnessProgress
                    )
                }
            }
        }

        DisposableEffect(audioHelper) {
            onDispose {
                audioHelper.dispose()
                brightnessHelper.dispose()
            }
        }
    }
}

@Composable
private fun VerticalProgress(
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
            size = Size(size.width, height = (progress * size.height)),
            topLeft = Offset(0.dp.toPx(), ((1 - progress) * size.height))
        )
        // background
        drawRect(
            color = backgroundColor,
            size = Size(width = size.width, height = ((1 - progress) * size.height)),
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