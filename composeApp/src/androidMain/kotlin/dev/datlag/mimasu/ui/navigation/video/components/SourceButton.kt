package dev.datlag.mimasu.ui.navigation.video.components

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.localContentColor
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentSet
import kotlin.time.Duration.Companion.minutes

@Composable
fun SourceButton(
    controlsState: ControlsState,
    viewModel: VideoViewModel,
    modifier: Modifier = Modifier,
    color: Color = Platform.localContentColor()
) {
    val sources by viewModel.allInfo.collectAsStateWithLifecycle()
    val selectedInfo by viewModel.selectedInfo.collectAsStateWithLifecycle()

    var dialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            dialog = !dialog
            controlsState.showControls(1.minutes)
        },
        modifier = modifier,
        enabled = sources.isNotEmpty(),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = color
        )
    ) {
        MaterialSymbols(
            name = MaterialSymbols.TRANSLATE,
            contentDescription = null
        )
    }

    if (dialog) {
        SourceBottomSheet(
            current = selectedInfo,
            choices = sources.toPersistentSet(),
            onDismissRequest = {
                dialog = false
                controlsState.showControls()
            },
            onSelectChoice = viewModel::selectInfo
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SourceBottomSheet(
    current: VideoViewModel.SourceInfo?,
    choices: ImmutableCollection<VideoViewModel.SourceInfo>,
    onDismissRequest: () -> Unit,
    onSelectChoice: (VideoViewModel.SourceInfo) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(choices.toImmutableList()) { info ->
                TextButton(
                    modifier = Modifier.fillParentMaxWidth(),
                    onClick = {
                        onSelectChoice(info)
                        onDismissRequest()
                    }
                ) {
                    val selected = remember(info, current) {
                        info == current
                    }

                    Text(
                        text = "${info.sourceTitle}: ${info.sourceLocale?.ifBlank { null } ?: info.locale}",
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