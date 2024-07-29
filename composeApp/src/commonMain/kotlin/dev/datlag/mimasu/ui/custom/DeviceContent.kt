package dev.datlag.mimasu.ui.custom

import androidx.compose.runtime.Composable
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.rememberIsTv

@Composable
fun DeviceContent(
    common: @Composable () -> Unit,
    jvm: @Composable () -> Unit = common,
    android: @Composable () -> Unit = jvm,
    tv: @Composable () -> Unit = android,
    desktop: @Composable () -> Unit = jvm,
    apple: @Composable () -> Unit = common,
    ios: @Composable () -> Unit = apple,
    mac: @Composable () -> Unit = apple,
) = when {
    Platform.isAndroid -> {
        if (Platform.rememberIsTv()) {
            tv()
        } else {
            android()
        }
    }
    Platform.isDesktopJvm -> desktop()
    Platform.isIOS -> ios()
    Platform.isMacOS -> mac()
    else -> common()
}

@Composable
fun DeviceContent(
    tv: @Composable () -> Unit,
    common: @Composable () -> Unit
) = DeviceContent(
    common = common,
    jvm = common,
    tv = tv,
    apple = common,
)