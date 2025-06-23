@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package dev.datlag.mimasu.ui.navigation.video

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import dev.datlag.mimasu.common.detectPinchGestures
import dev.datlag.mimasu.common.hazeEffect
import dev.datlag.mimasu.common.merge
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.other.rememberPiPHelper
import dev.datlag.mimasu.ui.custom.MaterialSymbols
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

    val data by videoViewModel.watchData.collectAsStateWithLifecycle()
    val sources by videoViewModel.selectedSource.collectAsStateWithLifecycle(emptyList())
    var streamIndex by remember(sources) { mutableIntStateOf(0) }
    val sourceUrl = remember(sources, streamIndex) {
        sources.elementAtOrNull(streamIndex)
    }
    val metadata = remember(data) {
        MediaMetadata.Builder()
            .setMediaType(MediaMetadata.MEDIA_TYPE_VIDEO)
            .setTitle(data?.title)
            .setSubtitle(data?.subTitle)
            .setGenre(data?.genre)
            .setAlbumTitle(data?.groupTitle)
            .setArtworkUri(data?.artworkUri?.toUri())
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

    val pipHelper = rememberPiPHelper()
    val pipActive by PiPHelper.active.collectAsStateWithLifecycle()
    var videoViewBounds by remember { mutableStateOf(Rect()) }

    LaunchedEffect(playerWrapper) {
        playerWrapper.onError {
            if (sources.size - 1 > streamIndex) {
                streamIndex++
            }
        }
    }

    LaunchedEffect(playerWrapper) {
        playerWrapper.onFirstFrame {
            windowController.isSystemBarsVisible = false
            windowController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
        modifier = Modifier.fillMaxSize().background(Color.Black),
        containerColor = Color.Black,
        contentColor = Color.White,
        topBar = {
            TopControls(
                state = controlsState,
                pipActive = pipActive,
                data = data,
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack
            )
        },
        bottomBar = {
            BottomControls(
                controlsState = controlsState,
                pipActive = pipActive,
                state = progressState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
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
                },
            contentAlignment = Alignment.Center
        ) {
            val sizeModifier = if (isZoomed) {
                Modifier.fillMaxSize().scale(zoom.coerceIn(0.75F, 1F))
            } else {
                Modifier.aspectRatio(aspectRatio).scale(max(zoom, 0.95F))
            }
            val hazeState = rememberHazeState()

            AndroidExternalSurface(
                modifier = sizeModifier.hazeSource(hazeState).onGloballyPositioned {
                    videoViewBounds = it.boundsInWindow().toAndroidRectF().toRect()
                },
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
                hazeState = hazeState,
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

            ExtraControls(
                controlsState = controlsState,
                hazeState = hazeState,
                player = playerWrapper,
                viewModel = videoViewModel,
                pipHelper = pipHelper,
                pipActive = pipActive,
                modifier = Modifier
                    .padding(bottom = contentPadding.calculateBottomPadding())
                    .align(Alignment.BottomCenter),
                enterPiP = {
                    pipHelper.enter(aspectRatio, videoViewBounds)
                }
            )
        }
    }

    DisposableEffect(windowController) {
        onDispose {
            windowController.isSystemBarsVisible = true
            windowController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            windowController.clearWindowFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON and WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
