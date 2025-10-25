package dev.datlag.mimasu.common

import android.app.Activity
import android.os.Build
import androidx.compose.ui.window.DialogProperties
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.video.VideoLayout
import android.content.res.Configuration
import android.view.View
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import dev.datlag.mimasu.ui.common.findActivity
import dev.datlag.tooling.deleteSafely
import dev.datlag.tooling.existsSafely
import java.io.File

fun Activity.isInPiPMode(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.isInPictureInPictureMode
    } else {
        false
    }
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

fun VideoLayout.Companion.fromOrientation(orientation: Int): VideoLayout {
    return when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> VideoLayout.Portrait
        Configuration.ORIENTATION_LANDSCAPE -> VideoLayout.Landscape
        else -> VideoLayout.Unknown
    }
}

fun VideoLayout.Companion.requestedOrOrientation(requested: VideoLayout, orientation: Int): VideoLayout {
    return when (requested) {
        !is VideoLayout.Unknown -> requested
        else -> fromOrientation(orientation)
    }
}

@Composable
fun rememberActivity(
    view: View = LocalView.current
): Activity? {
    return LocalActivity.current ?: view.context?.findActivity() ?: LocalContext.current.findActivity()
}

fun File.deleteRecursivelySafely(): Boolean = walkBottomUp().fold(true) { res, it ->
    (it.deleteSafely(res) || !it.existsSafely(res)) && res
}