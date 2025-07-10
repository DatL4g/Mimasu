package dev.datlag.mimasu.ui.navigation.video.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlaybackSpeedState
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.localContentColor
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration.Companion.minutes

@OptIn(UnstableApi::class)
@Composable
fun PlaybackSpeedButton(
    controlsState: ControlsState,
    player: Player,
    modifier: Modifier = Modifier,
    color: Color = Platform.localContentColor(),
    speedSelection: ImmutableCollection<Float> = persistentSetOf(0.5F, 0.75F, 1.0F, 1.25F, 1.5F, 1.75F, 2.0F)
) {
    val state = rememberPlaybackSpeedState(player)
    var dialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            dialog = !dialog
            controlsState.showControls(1.minutes)
        },
        modifier = modifier,
        enabled = speedSelection.isNotEmpty() && state.isEnabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = color
        )
    ) {
        MaterialSymbols(
            name = when (state.playbackSpeed) {
                0.5F -> MaterialSymbols.SPEED_ZERO_FIVE
                0.75F -> MaterialSymbols.SPEED_ZERO_SEVEN_FIVE
                1.25F -> MaterialSymbols.SPEED_ONE_TWO_FIVE
                1.5F -> MaterialSymbols.SPEED_ONE_FIVE
                1.75F -> MaterialSymbols.SPEED_ONE_SEVEN_FIVE
                2F -> MaterialSymbols.SPEED_TWO
                else -> MaterialSymbols.SPEED
            },
            contentDescription = null
        )
    }

    if (dialog) {
        SpeedBottomSheet(
            current = state.playbackSpeed,
            choices = speedSelection,
            onDismissRequest = {
                dialog = false
                controlsState.showControls()
            },
            onSelectChoice = state::updatePlaybackSpeed
        )
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpeedBottomSheet(
    current: Float,
    choices: ImmutableCollection<Float>,
    onDismissRequest: () -> Unit,
    onSelectChoice: (Float) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(choices.toImmutableList()) { speed ->
                TextButton(
                    modifier = Modifier.fillParentMaxWidth(),
                    onClick = {
                        onSelectChoice(speed)
                        onDismissRequest()
                    }
                ) {
                    val selected = remember(speed, current) {
                        speed == current
                    }

                    Text(
                        text = "%.2fx".format(speed),
                        fontWeight = if (selected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Light
                        }
                    )
                }
            }
        }
    }
}