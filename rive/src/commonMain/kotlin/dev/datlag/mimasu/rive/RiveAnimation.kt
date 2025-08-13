package dev.datlag.mimasu.rive

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
expect fun RiveAnimation(
    bytes: ByteArray,
    artboardName: String? = null,
    animationName: String? = null,
    stateMachineName: String? = null,
    autoplay: Boolean = true,
    fit: RiveFit = RiveFit.Contain,
    alignment: RiveAlignment = RiveAlignment.Center,
    loop: RiveLoop = RiveLoop.Auto,
    modifier: Modifier = Modifier,
    onUnavailable: () -> Unit = { },
    state: (RiveState) -> Unit
)