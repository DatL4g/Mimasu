package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import dev.datlag.kast.Kast
import dev.datlag.mimasu.common.detectPinchGestures
import dev.datlag.mimasu.common.detectSingleTap
import dev.datlag.mimasu.common.rememberCronetEngine
import dev.datlag.mimasu.core.MimasuConnection
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.withDI
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

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

    val playerWrapper = remember(cronetEngine, cache) {
        PlayerWrapper(
            context = context,
            castContext = Kast.castContext,
            cronetEngine = cronetEngine,
            cache = cache
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

    var isZoomed by remember(isCasting) {
        mutableStateOf(false)
    }
    var zoom by remember(isCasting) {
        mutableFloatStateOf(1F)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        val playerState = rememberVideoPlayerState(playerWrapper)

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
                modifier = sizeModifier.background(Color.Black),
                isSecure = videoPlayerSecure,
                onInit = {
                    onSurface { surface, _, _ ->
                        playerWrapper.setVideoSurface(surface)
                    }
                }
            )

            VolumeBrightnessControl(
                modifier = Modifier.matchParentSize(),
                onDoubleClickLeft = {
                    playerWrapper.seekBack()
                },
                onDoubleClickRight = {
                    playerWrapper.seekForward()
                },
                onTap = {
                    playerState.toggleControls()
                }
            )

            PlayerControls(
                state = playerState,
                modifier = Modifier.matchParentSize().padding(contentPadding),
                onRewind = playerWrapper::seekBack,
                onPlayPause = playerWrapper::togglePlay,
                onForward = playerWrapper::seekForward,
                onSeekFinished = playerWrapper::seekTo
            )
        }
    }

    /*AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        factory = { viewContext ->
            MimasuPlayerView(viewContext)
        },
        update = { player ->
            player.setSecure(videoPlayerSecure)
            player.isSoundEffectsEnabled = false
            player.keepScreenOn = true // change to be play store compliant
            player.player = playerWrapper
        }
    )*/
}

