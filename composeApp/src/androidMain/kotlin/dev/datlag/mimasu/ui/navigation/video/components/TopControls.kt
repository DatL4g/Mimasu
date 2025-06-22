package dev.datlag.mimasu.ui.navigation.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.video.states.ControlsState
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopControls(
    state: ControlsState,
    data: VideoViewModel.WatchData?,
    modifier: Modifier = Modifier,
    pipActive: Boolean = PiPHelper.active.value,
    onBack: () -> Unit
) {
    val visibility by state.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        modifier = modifier,
        visible = visibility && !pipActive,
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
                Text(text = data?.title ?: data?.groupTitle ?: data?.subTitle ?: "")
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )
    }
}