package dev.datlag.mimasu.common

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.DialogProperties
import dev.datlag.mimasu.ui.navigation.Navigation

@OptIn(ExperimentalComposeUiApi::class)
actual fun Navigation.Video.dialogProperties(): DialogProperties {
    return DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
        usePlatformDefaultWidth = false,
        usePlatformInsets = false,
        useSoftwareKeyboardInset = false
    )
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun Navigation.Login.dialogProperties(): DialogProperties {
    return DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
        usePlatformDefaultWidth = false,
        usePlatformInsets = false,
        useSoftwareKeyboardInset = false
    )
}