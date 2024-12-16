package dev.datlag.mimasu.ui.navigation.screen.movie.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.typography

@Composable
fun DescriptionSection(
    value: String?,
    modifier: Modifier = Modifier,
    fallbackValue: String? = null,
) {
    val description = remember(value, fallbackValue) {
        value?.ifBlank { null } ?: fallbackValue?.ifBlank { null }
    }

    if (!description.isNullOrBlank()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlatformText(
                modifier = Modifier.fillMaxWidth(),
                text = "Description",
                style = Platform.typography().headlineSmall
            )
            SelectionContainer {
                PlatformText(
                    modifier = Modifier.fillMaxWidth(),
                    text = description,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}