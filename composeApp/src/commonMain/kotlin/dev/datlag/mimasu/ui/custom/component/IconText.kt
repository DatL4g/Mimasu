package dev.datlag.mimasu.ui.custom.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.datlag.tooling.compose.platform.PlatformIcon
import dev.datlag.tooling.compose.platform.PlatformText

@Composable
fun IconText(
    icon: ImageVector,
    text: CharSequence
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        PlatformIcon(
            imageVector = icon,
            contentDescription = null
        )
        when (text) {
            is AnnotatedString -> PlatformText(text = text)
            else -> PlatformText(text = text.toString())
        }
    }
}