package dev.datlag.mimasu.ui.custom.login.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@Composable
internal expect fun LoginAppImage(
    typingEmail: Boolean,
    typingPassword: Boolean,
    imagePainter: Painter,
    imageModifier: Modifier = Modifier,
    riveModifier: Modifier = Modifier
)