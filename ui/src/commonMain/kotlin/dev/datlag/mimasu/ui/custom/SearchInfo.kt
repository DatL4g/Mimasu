package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.localContentColor

@Composable
fun SearchInfo(
    iconName: String,
    text: String,
    modifier: Modifier = Modifier,
    iconTint: Color = Platform.localContentColor()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MaterialSymbols(
            modifier = Modifier.size(96.dp),
            name = iconName,
            contentDescription = null,
            tint = iconTint
        )
        PlatformText(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}