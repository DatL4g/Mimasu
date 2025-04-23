package dev.datlag.mimasu.ui.navigation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.MaterialSymbols.invoke
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
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
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}