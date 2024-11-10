package dev.datlag.mimasu.ui.navigation.screen.video

import android.graphics.Rect
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.toRect
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import dev.datlag.kast.Kast
import dev.datlag.mimasu.common.detectPinchGestures
import dev.datlag.mimasu.common.rememberCronetEngine
import dev.datlag.mimasu.core.MimasuConnection
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.navigation.screen.video.components.BottomControls
import dev.datlag.mimasu.ui.navigation.screen.video.components.CenterControls
import dev.datlag.mimasu.ui.navigation.screen.video.components.TopControls
import dev.datlag.mimasu.ui.navigation.screen.video.components.VolumeBrightnessControl
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.withDI
import kotlin.math.max
import kotlin.math.min

@OptIn(UnstableApi::class)
@Composable
actual fun VideoScreen(component: VideoComponent) = withDI(component.di) {
    val mediaItem = remember {
        MediaItem.Builder()
            .setUri("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8")
            .build()
    }
    val context = LocalContext.current
    val cronetEngine = rememberCronetEngine()
    val cache by rememberInstance<Cache>()
    val windowController = rememberWindowController()

    val playerWrapper = remember(cronetEngine, cache) {
        PlayerWrapper(
            context = context,
            castContext = Kast.castContext,
            cronetEngine = cronetEngine,
            cache = cache,
            onFirstFrame = {
                windowController.isSystemBarsVisible = false
                windowController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                windowController.addWindowFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        )
    }

    LaunchedEffect(playerWrapper, mediaItem) {
        playerWrapper.setMediaItem(mediaItem)
        playerWrapper.prepare()
    }

    DisposableEffect(playerWrapper) {
        onDispose {
            playerWrapper.release()
        }
    }

    val videoPlayerSecure by MimasuConnection.isVideoPlayerSecure.collectAsStateWithLifecycle()
    val aspectRatio by playerWrapper.aspectRatio.collectAsStateWithLifecycle()
    val isCasting by playerWrapper.usingCastPlayer.collectAsStateWithLifecycle()
    val pipHelper = remember(context) { PiPHelper(context) }
    val pipActive by PiPHelper.active.collectAsStateWithLifecycle()
    var videoViewBounds by remember {
        mutableStateOf(Rect())
    }

    LaunchedEffect(videoPlayerSecure) {
        if (videoPlayerSecure) {
            windowController.addWindowFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            windowController.clearWindowFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    var isZoomed by remember(isCasting) {
        mutableStateOf(false)
    }
    var zoom by remember(isCasting) {
        mutableFloatStateOf(1F)
    }
    val playerState = rememberVideoPlayerState(playerWrapper)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopControls(
                state = playerState,
                pipActive = pipActive
            )
        },
        bottomBar = {
            BottomControls(
                state = playerState,
                pipActive = pipActive
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectPinchGestures(
                        pass = PointerEventPass.Initial,
                        onGesture = { _, newZoom ->
                            zoom *= newZoom
                        },
                        onGestureEnd = {
                            if (zoom > 1.2F) {
                                isZoomed = true
                            } else if (zoom < 0.8F) {
                                isZoomed = false
                            }

                            zoom = 1F
                        }
                    )
                }
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            val sizeModifier = if (isZoomed) {
                Modifier.fillMaxSize().scale(min(max(zoom, 0.75F), 1F))
            } else {
                Modifier.aspectRatio(aspectRatio).scale(max(zoom, 0.95F))
            }

            AndroidExternalSurface(
                modifier = sizeModifier.background(Color.Black).onGloballyPositioned {
                    videoViewBounds = it.boundsInWindow().toAndroidRectF().toRect()
                },
                isSecure = videoPlayerSecure,
                onInit = {
                    onSurface { surface, _, _ ->
                        playerWrapper.setVideoSurface(surface)
                    }
                }
            )

            VolumeBrightnessControl(
                state = playerState,
                modifier = Modifier.matchParentSize(),
                contentPadding = contentPadding,
            )

            CenterControls(
                state = playerState,
                modifier = Modifier.matchParentSize()
            )
        }
    }

    LaunchedEffect(pipHelper) {
        PiPHelper.onEnter {
            pipHelper.enter(aspectRatio, videoViewBounds)
        }
    }

    DisposableEffect(PiPHelper) {
        onDispose {
            videoViewBounds = Rect()
            PiPHelper.clearEnter()
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

