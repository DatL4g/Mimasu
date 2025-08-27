package dev.datlag.mimasu.tv.ui.navigation.video

import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.rememberDrawerState
import dev.datlag.mimasu.ui.common.asMediaMetaData
import dev.datlag.mimasu.ui.common.handleDPadKeyEvents
import dev.datlag.mimasu.ui.common.handlePlayerKeyEvents
import dev.datlag.mimasu.ui.custom.rememberWindowController
import dev.datlag.mimasu.ui.custom.video.rememberPlayerWrapper
import dev.datlag.mimasu.ui.custom.video.states.rememberControlsState
import dev.datlag.mimasu.ui.custom.video.states.rememberPlayPauseButtonState
import dev.datlag.mimasu.ui.custom.video.states.rememberProgressState
import dev.datlag.mimasu.ui.custom.video.states.rememberSeekState
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.compose.LaunchedMain
import dev.datlag.tooling.compose.MainThread
import dev.datlag.tooling.compose.ifTrue
import kotlin.time.Duration.Companion.seconds

@MainThread
@OptIn(UnstableApi::class)
@Composable
internal fun Video() {
    val videoViewModel = kodeinViewModel<VideoViewModel>()
    val windowController = rememberWindowController()
    val playerWrapper = rememberPlayerWrapper()
    val controlsState = rememberControlsState(5.seconds)
    val playPauseState = rememberPlayPauseButtonState(playerWrapper)
    val seekState = rememberSeekState(playerWrapper)
    val progressState = rememberProgressState(playerWrapper)
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val type by videoViewModel.watchType.collectAsState()
    val sources by videoViewModel.selectedSource.collectAsState(emptyList())
    var streamIndex by remember(sources) { mutableIntStateOf(0) }
    val sourceUrl = remember(sources, streamIndex) {
        sources.elementAtOrNull(streamIndex)
    }
    val metadata = remember(type) {
        type.asMediaMetaData()
    }
    val mediaItem = remember(sourceUrl, metadata) {
        sourceUrl?.let {
            MediaItem.Builder()
                .setUri(it)
                .setMediaMetadata(metadata)
                .build()
        }
    }
    val context = LocalContext.current
    var mediaSession by remember { mutableStateOf<MediaSession?>(null) }

    LaunchedMain(playerWrapper) {
        mediaSession?.release()
        mediaSession = MediaSession.Builder(context, ForwardingPlayer(playerWrapper)).build()

        playerWrapper.onError {
            if (sources.size - 1 > streamIndex) {
                streamIndex++
            }
        }
    }

    LaunchedMain(playerWrapper) {
        playerWrapper.onFinish {
            type?.let {
                videoViewModel.finish(it)
            }
        }
    }

    LaunchedMain(playerWrapper) {
        playerWrapper.onFirstFrame {
            windowController.addWindowFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedMain(playerWrapper, mediaItem) {
        if (mediaItem != null) {
            playerWrapper.setMediaItem(mediaItem)
            playerWrapper.prepare()
        }
    }

    DisposableEffect(playerWrapper) {
        onDispose {
            playerWrapper.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .ifTrue(drawerState.currentValue == DrawerValue.Closed) {
                handleDPadKeyEvents(
                    controlsState = controlsState,
                    playPauseButtonState = playPauseState,
                    seekState = seekState
                )
            }
            .handlePlayerKeyEvents(
                playPauseButtonState = playPauseState,
                seekState = seekState
            )
            .focusGroup()
    ) {
        AndroidExternalSurface(
            modifier = Modifier.fillMaxSize().focusable(),
            onInit = {
                onSurface { surface, _, _ ->
                    playerWrapper.setVideoSurface(surface)

                    surface.onChanged { _, _ ->
                        playerWrapper.setVideoSurface(surface)
                    }
                    surface.onDestroyed {
                        playerWrapper.clearVideoSurface()
                    }
                }
            }
        )
        PlayerControls(
            videoViewModel = videoViewModel,
            playPauseButtonState = playPauseState,
            progressState = progressState,
            controlsState = controlsState,
            watchType = type,
            drawerState = drawerState,
            modifier = Modifier.fillMaxSize()
        )
    }

    DisposableEffect(windowController) {
        onDispose {
            windowController.clearWindowFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaSession?.release()
            mediaSession = null
        }
    }
}