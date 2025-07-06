package dev.datlag.mimasu.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.provider.Settings
import android.view.Window
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.module.PlatformModule
import dev.datlag.mimasu.ui.navigation.Navigation
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

fun Activity.isInPiPMode(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.isInPictureInPictureMode
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
fun DIAware.videoCache(): Cache {
    val instance by this.instance<Cache>()
    return instance
}

@OptIn(UnstableApi::class)
fun DirectDI.videoCache(): Cache {
    return this.instance<Cache>()
}

actual fun Navigation.Video.dialogProperties(): DialogProperties {
    return DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false
    )
}

actual fun Navigation.Login.dialogProperties(): DialogProperties {
    return DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false
    )
}