package dev.datlag.mimasu.common

import android.app.Activity
import android.os.Build
import androidx.compose.ui.window.DialogProperties
import dev.datlag.mimasu.ui.navigation.Navigation

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