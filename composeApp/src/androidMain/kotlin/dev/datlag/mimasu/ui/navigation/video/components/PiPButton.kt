package dev.datlag.mimasu.ui.navigation.video.components

import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.localContentColor

@Composable
fun PiPButton(
    pipHelper: PiPHelper,
    modifier: Modifier = Modifier,
    color: Color = Platform.localContentColor(),
    enterPiP: () -> Unit
) {
    val enabled by pipHelper.enabled.collectAsStateWithLifecycle()

    IconButton(
        onClick = enterPiP,
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = color
        )
    ) {
        MaterialSymbols(
            name = MaterialSymbols.PIP,
            contentDescription = null
        )
    }
}