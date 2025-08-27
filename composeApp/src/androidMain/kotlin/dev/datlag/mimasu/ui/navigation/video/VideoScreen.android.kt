@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package dev.datlag.mimasu.ui.navigation.video

import android.content.pm.ActivityInfo
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import dev.datlag.mimasu.common.detectPinchGestures
import dev.datlag.mimasu.common.fromOrientation
import dev.datlag.mimasu.common.rememberActivity
import dev.datlag.mimasu.common.requestedOrOrientation
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.other.rememberPiPHelper
import dev.datlag.mimasu.ui.common.asMediaMetaData
import dev.datlag.mimasu.ui.common.handleDPadKeyEvents
import dev.datlag.mimasu.ui.common.handlePlayerKeyEvents
import dev.datlag.mimasu.ui.common.merge
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.rememberWindowController
import dev.datlag.mimasu.ui.custom.video.rememberPlayerWrapper
import dev.datlag.mimasu.ui.custom.video.states.rememberControlsState
import dev.datlag.mimasu.ui.custom.video.states.rememberPlayPauseButtonState
import dev.datlag.mimasu.ui.custom.video.states.rememberPresentationState
import dev.datlag.mimasu.ui.custom.video.states.rememberProgressState
import dev.datlag.mimasu.ui.custom.video.states.rememberSeekState
import dev.datlag.mimasu.ui.navigation.video.components.BottomControls
import dev.datlag.mimasu.ui.navigation.video.components.CenterControls
import dev.datlag.mimasu.ui.navigation.video.components.ExtraControls
import dev.datlag.mimasu.ui.navigation.video.components.FullscreenEnter
import dev.datlag.mimasu.ui.navigation.video.components.TopControls
import dev.datlag.mimasu.ui.navigation.video.components.VolumeBrightnessControl
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.compose.LaunchedMain
import dev.datlag.tooling.compose.MainThread
import dev.datlag.tooling.compose.ifFalse
import kotlin.math.max

@MainThread
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = rememberActivity()
    var mediaSession by remember { mutableStateOf<MediaSession?>(null) }

    val pipHelper = rememberPiPHelper()
    val pipActive by PiPHelper.active.collectAsStateWithLifecycle()
    var videoViewBounds by remember { mutableStateOf(Rect()) }
    var handleWindowController by remember(playerWrapper) { mutableStateOf(false) }
    var requestedLayout by remember { mutableStateOf<VideoLayout>(VideoLayout.Unknown) }
    var layout by remember { mutableStateOf<VideoLayout>(VideoLayout.Unknown) }

    LaunchedMain(requestedLayout, activity) {
        if (requestedLayout is VideoLayout.Landscape) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

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
            handleWindowController = true
            windowController.addWindowFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedMain(playerWrapper, mediaItem) {
        if (mediaItem != null) {
            playerWrapper.setMediaItem(mediaItem)
            playerWrapper.prepare()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    playerWrapper.releaseCasting()
                }
                else -> { }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            playerWrapper.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler {
        onBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .handleDPadKeyEvents(
                controlsState = controlsState,
                playPauseButtonState = playPauseState,
                seekState = seekState
            )
            .handlePlayerKeyEvents(
                playPauseButtonState = playPauseState,
                seekState = seekState
            ),
        topBar = {
            TopControls(
                state = controlsState,
                requestedLayout = requestedLayout,
                layout = layout,
                pipActive = pipActive,
                watchType = type,
                modifier = Modifier.fillMaxWidth(),
                exitFullscreen = {
                    requestedLayout = VideoLayout.Unknown
                },
                onBack = onBack
            )
        },
        bottomBar = {
            BottomControls(
                controlsState = controlsState,
                layout = layout,
                pipActive = pipActive,
                state = progressState,
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = {
            if (layout is VideoLayout.Portrait) {
                ExtraControls(
                    controlsState = controlsState,
                    layout = layout,
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
        val orientation = rememberOrientation()

        VideoInfo(
            watchType = type,
            contentPadding = contentPadding,
            requestedLayout = VideoLayout.requestedOrOrientation(requestedLayout, orientation)
        ) { info ->
            LaunchedMain(handleWindowController, info, windowController) {
                layout = info.layout

                if (handleWindowController) {
                    if (info.layout.isPortraitOrUnknown) {
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
                    .ifFalse(info.layout is VideoLayout.Portrait) {
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
                val zoomScale = remember(isZoomed, zoom, info.layout) {
                    if (isZoomed && info.layout !is VideoLayout.Portrait) {
                        zoom.coerceIn(0.75F, 1F)
                    } else {
                        max(zoom, 0.95F)
                    }
                }
                val roundedShape = remember(zoomScale, isZoomed, info.layout) {
                    if (zoomScale >= 1F || info.layout is VideoLayout.Portrait) {
                        RoundedCornerShape(0.dp)
                    } else {
                        val maxRound = 20.dp
                        val minZoom = if (isZoomed) 0.75F else 0.95F
                        val normalizedZoom = ((zoomScale - minZoom) / (1F - minZoom)).coerceIn(0f, 1F)
                        val inverseNormalizedZoom = 1f - normalizedZoom

                        RoundedCornerShape(maxRound * inverseNormalizedZoom)
                    }
                }

                val sizeModifier = if (isZoomed && info.layout !is VideoLayout.Portrait) {
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
                    layout = info.layout,
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

                if (info.layout !is VideoLayout.Portrait) {
                    ExtraControls(
                        modifier = Modifier
                            .padding(bottom = contentPadding.calculateBottomPadding())
                            .align(Alignment.BottomCenter),
                        controlsState = controlsState,
                        layout = layout,
                        player = playerWrapper,
                        viewModel = videoViewModel,
                        pipHelper = pipHelper,
                        pipActive = pipActive,
                        enterPiP = {
                            pipHelper.enter(aspectRatio, videoViewBounds)
                        }
                    )
                }

                if (info.layout is VideoLayout.Portrait) {
                    FullscreenEnter(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        controlsState = controlsState,
                        onEnter = {
                            requestedLayout = VideoLayout.Landscape
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
            mediaSession?.release()
            mediaSession = null
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
