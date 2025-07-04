@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package dev.datlag.mimasu.ui.navigation.video

import android.content.res.Configuration
import android.graphics.Rect
import android.view.WindowManager
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import dev.datlag.kast.Kast
import dev.datlag.kast.UnselectReason
import dev.datlag.mimasu.common.detectPinchGestures
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.other.rememberPiPHelper
import dev.datlag.mimasu.ui.common.merge
import dev.datlag.mimasu.ui.navigation.video.components.BottomControls
import dev.datlag.mimasu.ui.navigation.video.components.CenterControls
import dev.datlag.mimasu.ui.navigation.video.components.ExtraControls
import dev.datlag.mimasu.ui.navigation.video.components.TopControls
import dev.datlag.mimasu.ui.navigation.video.components.VolumeBrightnessControl
import dev.datlag.mimasu.ui.navigation.video.states.rememberControlsState
import dev.datlag.mimasu.ui.navigation.video.states.rememberPlayPauseButtonState
import dev.datlag.mimasu.ui.navigation.video.states.rememberPresentationState
import dev.datlag.mimasu.ui.navigation.video.states.rememberProgressState
import dev.datlag.mimasu.ui.navigation.video.states.rememberSeekState
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.compose.ifFalse
import kotlin.math.max

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun VideoScreen(onBack: () -> Unit) {
    val videoViewModel = kodeinViewModel<VideoViewModel>()
    val windowController = rememberWindowController()
    val playerWrapper = rememberPlayerWrapper()
    val presentationState = rememberPresentationState(playerWrapper)
    val controlsState = rememberControlsState()
    val progressState = rememberProgressState(playerWrapper)
    val playPauseState = rememberPlayPauseButtonState(playerWrapper)
    val seekState = rememberSeekState(playerWrapper)
    val isCasting by playerWrapper.usingCastPlayer.collectAsStateWithLifecycle()
    val videoSize by presentationState.videoSizeDp.collectAsStateWithLifecycle()
    val aspectRatio = remember(videoSize) {
        val height = videoSize?.height ?: return@remember 16F / 9F
        val width = videoSize?.width ?: return@remember 16F / 9F

        width / height
    }
    var isZoomed by remember(isCasting) {
        mutableStateOf(false)
    }
    var zoom by remember(isCasting) {
        mutableFloatStateOf(1F)
    }

    val type by videoViewModel.watchType.collectAsStateWithLifecycle()
    val sources by videoViewModel.selectedSource.collectAsStateWithLifecycle(emptyList())
    var streamIndex by remember(sources) { mutableIntStateOf(0) }
    val sourceUrl = remember(sources, streamIndex) {
        sources.elementAtOrNull(streamIndex)
    }
    val metadata = remember(type) {
        MediaMetadata.Builder()
            .setMediaType(MediaMetadata.MEDIA_TYPE_VIDEO)
            .setTitle(type?.title)
            .setSubtitle(type?.subTitle)
            .setGenre(type?.genre)
            .setAlbumTitle(type?.albumTitle)
            .build()
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

    val pipHelper = rememberPiPHelper()
    val pipActive by PiPHelper.active.collectAsStateWithLifecycle()
    var videoViewBounds by remember { mutableStateOf(Rect()) }
    var handleWindowController by remember(playerWrapper) { mutableStateOf(false) }
    var isInCompactMode by remember { mutableStateOf(false) }

    LaunchedEffect(playerWrapper) {
        mediaSession?.release()
        mediaSession = MediaSession.Builder(context, ForwardingPlayer(playerWrapper)).build()

        playerWrapper.onError {
            if (sources.size - 1 > streamIndex) {
                streamIndex++
            }
        }
    }

    LaunchedEffect(playerWrapper) {
        playerWrapper.onFinish {
            type?.let {
                videoViewModel.finish(it)
            }
        }
    }

    LaunchedEffect(playerWrapper) {
        playerWrapper.onFirstFrame {
            handleWindowController = true
            windowController.addWindowFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(playerWrapper, mediaItem) {
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

    BackHandler {
        onBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopControls(
                state = controlsState,
                isInCompactMode = isInCompactMode,
                pipActive = pipActive,
                watchType = type,
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack
            )
        },
        bottomBar = {
            BottomControls(
                controlsState = controlsState,
                isInCompactMode = isInCompactMode,
                pipActive = pipActive,
                state = progressState,
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = {
            if (isInCompactMode) {
                ExtraControls(
                    controlsState = controlsState,
                    isInCompactMode = true,
                    player = playerWrapper,
                    viewModel = videoViewModel,
                    pipHelper = pipHelper,
                    pipActive = pipActive,
                    enterPiP = {
                        pipHelper.enter(aspectRatio, videoViewBounds)
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { contentPadding ->
        VideoInfo(
            watchType = type,
            contentPadding = contentPadding,
            forceCompact = LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE
        ) { info ->
            LaunchedEffect(handleWindowController, info, windowController) {
                isInCompactMode = info.showingCompact

                if (handleWindowController) {
                    if (info.showingCompact) {
                        windowController.isSystemBarsVisible = true
                        windowController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                    } else {
                        windowController.isSystemBarsVisible = false
                        windowController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }
            }

            Box(
                modifier = info.modifier
                    .background(Color.Black)
                    .ifFalse(info.showingCompact) {
                        pointerInput(Unit) {
                            detectPinchGestures(
                                pass = PointerEventPass.Initial,
                                onGesture = { _, newZoom ->
                                    zoom *= newZoom
                                },
                                onGestureEnd = {
                                    if (zoom > 1.2F) {
                                        isZoomed = true
                                    } else if (zoom < 0.85F) {
                                        isZoomed = false
                                    }

                                    zoom = 1F
                                }
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val zoomScale = remember(isZoomed, zoom, info.showingCompact) {
                    if (isZoomed && !info.showingCompact) {
                        zoom.coerceIn(0.75F, 1F)
                    } else {
                        max(zoom, 0.95F)
                    }
                }
                val roundedShape = remember(zoomScale, isZoomed, info.showingCompact) {
                    if (zoomScale >= 1F || info.showingCompact) {
                        RoundedCornerShape(0.dp)
                    } else {
                        val maxRound = 20.dp
                        val minZoom = if (isZoomed) 0.75F else 0.95F
                        val normalizedZoom = ((zoomScale - minZoom) / (1F - minZoom)).coerceIn(0f, 1F)
                        val inverseNormalizedZoom = 1f - normalizedZoom

                        RoundedCornerShape(maxRound * inverseNormalizedZoom)
                    }
                }

                val sizeModifier = if (isZoomed && !info.showingCompact) {
                    Modifier.fillMaxSize().scale(zoomScale)
                } else {
                    Modifier.aspectRatio(aspectRatio).scale(zoomScale)
                }

                AndroidExternalSurface(
                    modifier = sizeModifier.onGloballyPositioned {
                        videoViewBounds = it.boundsInWindow().toAndroidRectF().toRect()
                    }.clip(roundedShape),
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

                VolumeBrightnessControl(
                    controlsState = controlsState,
                    isInCompactMode = info.showingCompact,
                    contentPadding = contentPadding.merge(PaddingValues(top = 16.dp)),
                    modifier = Modifier.matchParentSize()
                )

                CenterControls(
                    controlsState = controlsState,
                    state = playPauseState,
                    seekState = seekState,
                    pipActive = pipActive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )

                if (!info.showingCompact) {
                    ExtraControls(
                        modifier = Modifier
                            .padding(bottom = contentPadding.calculateBottomPadding())
                            .align(Alignment.BottomCenter),
                        controlsState = controlsState,
                        isInCompactMode = false,
                        player = playerWrapper,
                        viewModel = videoViewModel,
                        pipHelper = pipHelper,
                        pipActive = pipActive,
                        enterPiP = {
                            pipHelper.enter(aspectRatio, videoViewBounds)
                        }
                    )
                }
            }
        }
    }

    DisposableEffect(windowController) {
        onDispose {
            windowController.isSystemBarsVisible = true
            windowController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            windowController.clearWindowFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON and WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Kast.unselect(UnselectReason.stopped)

            mediaSession?.release()
            mediaSession = null
        }
    }
}
