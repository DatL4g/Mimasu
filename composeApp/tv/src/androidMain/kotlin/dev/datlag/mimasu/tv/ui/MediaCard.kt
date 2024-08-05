package dev.datlag.mimasu.tv.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface

@Composable
fun MediaCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = { },
    image: @Composable BoxScope.() -> Unit
) {
    StandardCardContainer(
        modifier = modifier,
        title = title,
        imageCard = {
            Surface(
                onClick = onClick,
                content = image
            )
        }
    )
}