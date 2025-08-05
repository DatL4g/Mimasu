package dev.datlag.mimasu.ui.custom.login.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
internal actual fun LoginAppImage(
    typingEmail: Boolean,
    typingPassword: Boolean,
    imagePainter: Painter,
    imageModifier: Modifier,
    riveModifier: Modifier
) {
    Image(
        painter = imagePainter,
        contentDescription = null,
        modifier = imageModifier,
        contentScale = ContentScale.Crop
    )
}