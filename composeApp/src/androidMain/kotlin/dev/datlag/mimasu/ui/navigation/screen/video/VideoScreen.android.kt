package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.annotation.OptIn
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import dev.datlag.kast.Kast
import dev.datlag.mimasu.common.detectPinchGestures
import dev.datlag.mimasu.common.rememberCronetEngine
import dev.datlag.mimasu.core.MimasuConnection
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.compose.ifTrue
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectPinchGestures(
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
            Modifier.fillMaxSize().scale(min(zoom, 1F))
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