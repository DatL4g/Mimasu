package dev.datlag.mimasu.ui.custom.login.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.WebElementView
import dev.datlag.mimasu.rive.RiveAnimation
import dev.datlag.mimasu.ui.LaunchedVirtualIO
import dev.datlag.mimasu.ui.UiRes
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun LoginAppImage(
    typingEmail: Boolean,
    typingPassword: Boolean,
    imagePainter: Painter,
    imageModifier: Modifier,
    riveModifier: Modifier
) {
    var bytes by rememberSaveable {
        mutableStateOf(ByteArray(0))
    }

    LaunchedVirtualIO(bytes) {
        if (bytes.isEmpty()) {
            bytes = UiRes.readBytes("files/rive/bunny_login.riv")
        }
    }

    if (bytes.isEmpty()) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
    } else {
        console.log("WebGL 1 Support: ${isWebGL1Supported()}")
        console.log("WebGL 2 Support: ${isWebGL2Supported()}")
        RiveAnimation(
            bytes = bytes,
            modifier = riveModifier
        ) { state ->

        }
    }
}

private fun isWebGL2Supported(): Boolean {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    return (canvas.getContext("webgl2") != null)
}

private fun isWebGL1Supported(): Boolean {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    // experimental-webgl covers old browsers
    return (canvas.getContext("webgl") ?: canvas.getContext("experimental-webgl")) != null
}