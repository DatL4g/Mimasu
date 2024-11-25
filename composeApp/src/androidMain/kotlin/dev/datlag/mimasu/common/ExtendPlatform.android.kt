package dev.datlag.mimasu.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.KeyEvent as AndroidKeyEvent
import android.view.Window
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.module.PlatformModule
import dev.datlag.mimasu.ui.navigation.screen.video.VideoPlayerState
import dev.datlag.tooling.Platform
import io.github.aakira.napier.Napier
import org.chromium.net.CronetEngine
import org.kodein.di.DIAware
import org.kodein.di.DirectDI
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Composable
actual fun Platform.githubAuthParams(): GitHubAuthParams? {
    val context = LocalContext.current
    return remember(context) {
        context.findActivity()
    } ?: context.findActivity()
}

tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.findActivity()
        else -> null
    }
}

tailrec fun Context.findWindow(): Window? = when (this) {
    is Activity -> window
    is ContextWrapper -> baseContext.findWindow()
    else -> null
}

fun Context.isActivityInPiPMode(): Boolean {
    val currentActivity = findActivity() ?: return false

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        currentActivity.isInPictureInPictureMode
    } else {
        false
    }
}

@OptIn(UnstableApi::class)
fun DIAware.cronetEngine(): CronetEngine? {
    val instance by this.instanceOrNull<PlatformModule.Cronet>()
    return instance?.engine
}

fun DirectDI.cronetEngine(): CronetEngine? {
    return this.instanceOrNull<PlatformModule.Cronet>()?.engine
}

@OptIn(UnstableApi::class)
@Composable
fun rememberCronetEngine(): CronetEngine? = with(localDI()) {
    return@with remember { this.cronetEngine() }
}

@Composable
fun Modifier.drawProgress(
    color: Color,
    progress: Float
): Modifier = drawWithContent {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)

        drawContent()

        drawRect(
            color = color,
            size = Size(size.width * progress, size.height),
            blendMode = BlendMode.SrcOut
        )

        restoreToCount(checkPoint)
    }
}

@Composable
fun Modifier.handleDPadKeyEvents(
    state: VideoPlayerState
) = this.handleDPadKeyEvents(
    onLeft = {
        if (!state.controlsVisible) {
            state.seekBack(showControls = false)
        }
        !state.controlsVisible
    },
    onRight = {
        if (!state.controlsVisible) {
            state.seekForward(showControls = false)
        }
        !state.controlsVisible
    },
    onUp = state::showControls,
    onDown = state::showControls,
    onEnter = state::togglePlayPause
)

@Composable
fun Modifier.handlePlayerShortcuts(
    state: VideoPlayerState
) = this.handlePlayerShortcuts(
    play = state::play,
    playPause = state::togglePlayPause,
    pause = state::pause,
    rewind = state::seekBack,
    forward = state::seekForward
)