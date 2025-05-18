package dev.datlag.mimasu.ui.navigation.login.components

import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimatedVectorDrawable
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import dev.datlag.mimasu.AppInitializer
import dev.datlag.mimasu.R
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
        val image = AnimatedImageVector.animatedVectorResource(R.drawable.animated_launcher)

        Image(
            painter = rememberAnimatedVectorPainter(image, false),
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
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