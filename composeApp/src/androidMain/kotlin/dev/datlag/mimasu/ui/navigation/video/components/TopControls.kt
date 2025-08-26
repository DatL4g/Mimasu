package dev.datlag.mimasu.ui.navigation.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.kast.ConnectionState
import dev.datlag.kast.Device
import dev.datlag.kast.DeviceType
import dev.datlag.kast.Kast
import dev.datlag.kast.UnselectReason
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.navigation.video.VideoLayout
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopControls(
    state: ControlsState,
    requestedLayout: VideoLayout,
    layout: VideoLayout,
    watchType: VideoViewModel.WatchType?,
    modifier: Modifier = Modifier,
    pipActive: Boolean = PiPHelper.active.value,
    exitFullscreen: () -> Unit,
    onBack: () -> Unit
) {
    val visibility by state.controlsVisibility.collectAsStateWithLifecycle()
    val castState by Kast.connectionState.collectAsStateWithLifecycle()
    var dialog by remember { mutableStateOf(false) }
    val castDevices by Kast.allAvailableDevices.collectAsStateWithLifecycle()

    AnimatedVisibility(
        modifier = modifier,
        visible = (visibility || layout is VideoLayout.Portrait) && !pipActive,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(
                    onClick = onBack
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.ARROW_BACK_IOS_NEW,
                        contentDescription = null
                    )
                }
            },
            title = {
                Text(text = watchType?.title ?: "")
            },
            colors = if (layout is VideoLayout.Portrait) {
                TopAppBarDefaults.topAppBarColors()
            } else {
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            },
            actions = {
                if (requestedLayout is VideoLayout.Landscape) {
                    IconButton(
                        onClick = exitFullscreen
                    ) {
                        MaterialSymbols(
                            name = MaterialSymbols.FULLSCREEN_EXIT,
                            contentDescription = null
                        )
                    }
                }
                if (Kast.isSupported) {
                    IconButton(
                        onClick = {
                            dialog = !dialog
                        },
                        enabled = castDevices.isNotEmpty()
                    ) {
                        MaterialSymbols(
                            name = if (castState is ConnectionState.DISCONNECTED) {
                                MaterialSymbols.CAST
                            } else {
                                MaterialSymbols.CAST_CONNECTED
                            },
                            contentDescription = null,
                            filled = castState !is ConnectionState.DISCONNECTED
                        )
                    }
                }
            }
        )
    }

    if (dialog) {
        CastDeviceBottomSheet(
            devices = castDevices.distinctBy { it.name to it.type },
            onDismissRequest = { dialog = false },
            onSelectDevice = {
                if (it.isSelected) {
                    Kast.unselect(UnselectReason.disconnected)
                } else {
                    Kast.select(it)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CastDeviceBottomSheet(
    devices: Collection<Device>,
    onDismissRequest: () -> Unit,
    onSelectDevice: (Device) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(devices.toImmutableList()) { device ->
                TextButton(
                    modifier = Modifier.fillParentMaxWidth(),
                    onClick = {
                        onSelectDevice(device)
                        onDismissRequest()
                    }
                ) {
                    MaterialSymbols(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        name = when (device.type) {
                            DeviceType.TV -> MaterialSymbols.TV
                            DeviceType.SPEAKER -> MaterialSymbols.SPEAKER
                            else -> MaterialSymbols.COMPUTER
                        },
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = device.name,
                        fontWeight = if (device.isSelected) {
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