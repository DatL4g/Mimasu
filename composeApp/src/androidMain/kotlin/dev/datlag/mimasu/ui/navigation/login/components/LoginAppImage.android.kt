package dev.datlag.mimasu.ui.navigation.login.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.datlag.mimasu.AppInitializer
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.rive.RiveAnimation

@Composable
actual fun LoginAppImage(
    typingEmail: Boolean,
    typingPassword: Boolean,
    imageModifier: Modifier,
    riveModifier: Modifier
) {
    val context = LocalContext.current
    var bytes by rememberSaveable {
        mutableStateOf(ByteArray(0))
    }
    val riveLoaded = remember(context) {
        AppInitializer.isRiveLoaded(context)
    }

    LaunchedEffect(bytes) {
        if (bytes.isEmpty()) {
            bytes = Res.readBytes("files/rive/bunny_login.riv")
        }
    }

    if (bytes.isEmpty() || !riveLoaded) {

    } else {
        RiveAnimation(
            bytes = bytes,
            modifier = riveModifier
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