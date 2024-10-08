package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import dev.datlag.kast.Kast
import dev.datlag.mimasu.common.rememberCronetEngine
import dev.datlag.mimasu.core.MimasuConnection
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.withDI

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

    AndroidView(
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
    )
}