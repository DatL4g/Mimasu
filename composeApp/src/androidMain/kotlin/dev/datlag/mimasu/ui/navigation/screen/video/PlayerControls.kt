package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import ir.mahozad.multiplatform.wavyslider.material3.WavySlider
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    contentProgress: Duration,
    contentDuration: Duration,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {

        }
        Spacer(modifier = Modifier.weight(1F))

        val interactionSource = remember { MutableInteractionSource() }
        var seeking by remember { mutableFloatStateOf((contentProgress / contentDuration).toFloat()) }
        WavySlider(
            modifier = Modifier.fillMaxWidth(),
            value = (contentProgress / contentDuration).toFloat(),
            onValueChange = {
                seeking = it
                onSeek(seeking)
            },
            onValueChangeFinished = {
                onSeekFinished(seeking)
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