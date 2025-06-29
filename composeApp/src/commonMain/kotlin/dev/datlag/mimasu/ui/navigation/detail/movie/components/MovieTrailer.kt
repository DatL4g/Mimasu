package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MovieTrailer(
    url: String?,
    modifier: Modifier = Modifier
)