package dev.datlag.mimasu.ui.custom.login.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import dev.datlag.mimasu.rive.RiveAnimation
import dev.datlag.mimasu.ui.LaunchedVirtualIO
import dev.datlag.mimasu.ui.UiRes

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun LoginAppImage(
    typingEmail: Boolean,
    typingPassword: Boolean,
    imagePainter: Painter,
    imageModifier: Modifier,
    riveModifier: Modifier
) {
    var fallback by remember { mutableStateOf(false) }
    var bytes by rememberSaveable {
        mutableStateOf(ByteArray(0))
    }

    LaunchedVirtualIO(bytes) {
        if (bytes.isEmpty()) {
            bytes = UiRes.readBytes("files/rive/bunny_login.riv")
        }
    }

    if (fallback || bytes.isEmpty()) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
    } else {
        RiveAnimation(
            bytes = bytes,
            modifier = riveModifier,
            onUnavailable = {
                fallback = true
            }
        ) { state ->

            state.setBoolean(
                stateMachineName = "State Machine 1",
                inputName = "isFocus",
                value = typingEmail
            )

            state.setBoolean(
                stateMachineName = "State Machine 1",
                inputName = "IsPassword",
                value = typingPassword
            )
        }
    }
}