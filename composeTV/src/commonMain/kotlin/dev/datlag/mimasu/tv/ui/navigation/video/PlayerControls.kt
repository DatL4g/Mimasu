package dev.datlag.mimasu.tv.ui.navigation.video

import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.tv.material3.DrawerState
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.Text
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.video.ProgressBar
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.custom.video.states.PlayPauseButtonState
import dev.datlag.mimasu.ui.custom.video.states.ProgressState
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.tooling.compose.LaunchedMain
import dev.datlag.tooling.compose.MainThread
import kotlinx.collections.immutable.toImmutableList

@MainThread
@OptIn(UnstableApi::class)
@Composable
internal fun PlayerControls(
    videoViewModel: VideoViewModel,
    playPauseButtonState: PlayPauseButtonState,
    progressState: ProgressState,
    controlsState: ControlsState,
    watchType: VideoViewModel.WatchType?,
    drawerState: DrawerState,
    modifier: Modifier = Modifier
) {
    val sources by videoViewModel.allInfo.collectAsState()
    val selectedInfo by videoViewModel.selectedInfo.collectAsState()
    val drawerFocus = remember { FocusRequester() }

    BackHandler(enabled = drawerState.currentValue == DrawerValue.Open) {
        drawerState.setValue(DrawerValue.Closed)
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            LazyColumn(
                modifier = Modifier.fillMaxHeight().focusRequester(drawerFocus),
                contentPadding = PaddingValues(32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                if (drawerState.currentValue == DrawerValue.Open) {
                    items(sources.toImmutableList()) { info ->
                        NavigationDrawerItem(
                            selected = info == selectedInfo,
                            onClick = {
                                videoViewModel.selectInfo(info)
                                drawerState.setValue(DrawerValue.Closed)
                            },
                            leadingContent = {
                                if (info == selectedInfo) {
                                    MaterialSymbols(
                                        modifier = Modifier.size(NavigationDrawerItemDefaults.IconSize),
                                        name = MaterialSymbols.CHECK,
                                        contentDescription = null
                                    )
                                }
                            },
                            content = {
                                Text(text = info.sourceLocale?.ifBlank { null } ?: info.locale ?: "")
                            },
                            supportingContent = info.sourceTitle?.let {
                                {
                                    Text(text = it)
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp)
        ) {
            val isVisible by controlsState.controlsVisibility.collectAsState()
            val focusRequester = remember { FocusRequester() }

            LaunchedMain(isVisible) {
                if (isVisible && drawerState.currentValue != DrawerValue.Open) {
                    focusRequester.requestFocus()
                }
            }

            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
                visible = isVisible,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = watchType?.title ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
                visible = isVisible,
                enter = slideInVertically { it / 2 } + fadeIn(),
                exit = slideOutVertically { it / 2 } + fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    val playPauseEnabled by playPauseButtonState.isEnabled.collectAsState()
                    val showPlay by playPauseButtonState.showPlay.collectAsState()

                    IconButton(
                        modifier = Modifier.focusRequester(focusRequester),
                        onClick = {
                            playPauseButtonState.onClick()
                        },
                        enabled = playPauseEnabled
                    ) {
                        if (showPlay) {
                            MaterialSymbols(
                                name = MaterialSymbols.PLAY_ARROW,
                                contentDescription = null,
                                filled = true
                            )
                        } else {
                            MaterialSymbols(
                                name = MaterialSymbols.PAUSE,
                                contentDescription = null,
                                filled = true
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            drawerState.setValue(DrawerValue.Open)
                            controlsState.hideControls()
                        },
                        enabled = sources.isNotEmpty()
                    ) {
                        MaterialSymbols(
                            name = MaterialSymbols.TRANSLATE,
                            contentDescription = null
                        )
                    }

                    ProgressBar(
                        controlsState = controlsState,
                        state = progressState
                    )
                }
            }
        }
    }
}