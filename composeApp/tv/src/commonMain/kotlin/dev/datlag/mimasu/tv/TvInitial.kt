package dev.datlag.mimasu.tv

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
expect fun TvInitial(
    profile: TvInitialNavigation,
    home: TvInitialNavigation,
    favorites: TvInitialNavigation,
    search: TvInitialSearch,
    content: @Composable () -> Unit
)

data class TvInitialNavigation(
    val selected: Boolean,
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

data class TvInitialSearch(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val onSearch: (String) -> Unit = { }
)