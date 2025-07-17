package dev.datlag.mimasu.ui.common

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.provider.Settings
import android.view.Window
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import app.rive.runtime.kotlin.core.RendererType
import app.rive.runtime.kotlin.core.Rive
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.GoogleAuthParams
import dev.datlag.mimasu.ui.Cronet
import dev.datlag.mimasu.ui.custom.video.states.ControlsState
import dev.datlag.mimasu.ui.custom.video.states.PlayPauseButtonState
import dev.datlag.mimasu.ui.custom.video.states.SeekState
import dev.datlag.mimasu.ui.other.ArchUtils
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.sekret.NativeLoader
import dev.datlag.tooling.scopeCatching
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toJavaLocalDate
import org.chromium.net.CronetEngine
import org.kodein.di.DIAware
import org.kodein.di.DirectDI
import org.kodein.di.instance
import org.kodein.di.instanceOrNull
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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

@Composable
actual fun rememberGitHubAuthParams(): GitHubAuthParams? {
    val context = LocalContext.current
    return remember(context) {
        context.findActivity()
    } ?: context.findActivity()
}

@Composable
actual fun rememberGoogleAuthParams(): GoogleAuthParams {
    val context = LocalContext.current
    val activity = remember(context) {
        context.findActivity()
    } ?: context.findActivity()

    return remember(activity) {
        GoogleAuthParams(context = activity ?: context, isRetrying = false)
    }
}

private fun Context.isRunningInTestLab(): Boolean {
    val testLabSetting = Settings.System.getString(contentResolver, "firebase.test.lab") ?: return false
    return when {
        testLabSetting.equals("true", ignoreCase = true) -> true
        testLabSetting == "1" -> true
        else -> testLabSetting.toBoolean()
    }
}

@Suppress("DEPRECATION")
private fun isRunningInTestHarness(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityManager.isRunningInUserTestHarness()
    } else {
        ActivityManager.isRunningInTestHarness()
    }
}

private fun Context.isLowRam(): Boolean {
    val activityManager = scopeCatching {
        this.getSystemService<ActivityManager>()
    }.getOrNull() ?: ContextCompat.getSystemService(this, ActivityManager::class.java)

    return activityManager?.isLowRamDevice ?: false
}

fun Context.supportsRive(): Boolean {
    return !isRunningInTestLab() && !isRunningInTestHarness() && !isLowRam() && ArchUtils.supportsRive()
}

fun Rive.initSafely(
    context: Context,
    defaultRenderer: RendererType = defaultRendererType
): Boolean {
    if (!context.supportsRive()) {
        return false
    }

    val riveClass = "app.rive.runtime.kotlin.core.Rive"
    val libName = scopeCatching {
        val clazz = Class.forName(riveClass)
        val field = clazz.getDeclaredField("RIVE_ANDROID")
        field.isAccessible = true
        field.get(null) as? String
    }.getOrNull()?.trim()?.ifBlank { null } ?: "rive-android"

    val libLoaded = NativeLoader.loadLibrary(context, libName)
    val rendererSet = this.defaultRendererType == defaultRenderer || scopeCatching {
        val clazz = Class.forName(riveClass)
        val field = clazz.getDeclaredField("defaultRendererType")
        field.isAccessible = true
        field.set(clazz, defaultRenderer)
    }.isSuccess

    return if (libLoaded && rendererSet) {
        initializeCppEnvironment()
        true
    } else {
        init(context, defaultRenderer)
        false
    }
}

@Composable
actual fun LocalDate?.formatMedium(fallbackFormat: String): String? {
    if (this == null) {
        return null
    }
    val formatter = remember {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    }
    val fallbackFormatter = remember(fallbackFormat) {
        LocalDate.Format {
            byUnicodePattern(fallbackFormat)
        }
    }

    return scopeCatching {
        this.toJavaLocalDate().format(formatter)
    }.getOrNull()?.ifBlank { null } ?: scopeCatching {
        fallbackFormatter.format(this)
    }.getOrNull()?.ifBlank { null }
}

fun DIAware.cronetEngine(): CronetEngine? {
    val instance by this.instanceOrNull<Cronet>()
    return instance?.engine
}

fun DirectDI.cronetEngine(): CronetEngine? {
    return this.instanceOrNull<Cronet>()?.engine
}

@OptIn(UnstableApi::class)
fun DIAware.videoCache(): Cache {
    val instance by this.instance<Cache>()
    return instance
}

@OptIn(UnstableApi::class)
fun DirectDI.videoCache(): Cache {
    return this.instance<Cache>()
}

@OptIn(UnstableApi::class)
@Composable
fun Modifier.handleDPadKeyEvents(
    controlsState: ControlsState,
    playPauseButtonState: PlayPauseButtonState,
    seekState: SeekState
): Modifier = handleDPadKeyEvents(
    onLeft = {
        if (!controlsState.controlsVisible) {
            seekState.seekBack()
        }
        !controlsState.controlsVisible
    },
    onRight = {
        if (!controlsState.controlsVisible) {
            seekState.seekForward()
        }
        !controlsState.controlsVisible
    },
    onUp = {
        if (!controlsState.controlsVisible) {
            controlsState.showControls()
            true
        } else {
            false
        }
    },
    onDown = {
        if (!controlsState.controlsVisible) {
            controlsState.showControls()
            true
        } else {
            false
        }
    },
    onEnter = {
        if (playPauseButtonState.isEnabled.value) {
            playPauseButtonState.onClick()
            true
        } else {
            if (!controlsState.controlsVisible) {
                controlsState.showControls()
                true
            } else {
                false
            }
        }
    }
)

@OptIn(UnstableApi::class)
@Composable
fun Modifier.handlePlayerKeyEvents(
    playPauseButtonState: PlayPauseButtonState,
    seekState: SeekState
): Modifier = handlePlayerKeyEvents(
    play = {
        playPauseButtonState.play()
        true
    },
    playPause = {
        if (playPauseButtonState.isEnabled.value) {
            playPauseButtonState.onClick()
        }
        true
    },
    pause = {
        playPauseButtonState.pause()
        true
    },
    rewind = {
        seekState.seekBack()
        true
    },
    forward = {
        seekState.seekForward()
        true
    }
)

fun VideoViewModel.WatchType?.asMediaMetaData(): MediaMetadata {
    return MediaMetadata.Builder()
        .setMediaType(MediaMetadata.MEDIA_TYPE_VIDEO)
        .setTitle(this?.title)
        .setSubtitle(this?.subTitle)
        .setGenre(this?.genre)
        .setAlbumTitle(this?.albumTitle)
        .build()
}

actual fun clipEntryOf(text: String): ClipEntry = ClipEntry(ClipData.newPlainText(null, text))