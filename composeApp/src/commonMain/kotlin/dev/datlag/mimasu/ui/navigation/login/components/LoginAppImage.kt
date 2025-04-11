package dev.datlag.mimasu.ui.navigation.login.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun LoginAppImage(
    typingEmail: Boolean,
    typingPassword: Boolean,
    imageModifier: Modifier = Modifier,
    riveModifier: Modifier = Modifier
)