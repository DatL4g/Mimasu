package dev.datlag.mimasu.ui.navigation.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun rememberAppImage(): Painter {
    return painterResource(Res.drawable.app_icon)
}