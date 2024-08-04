package dev.datlag.mimasu.tv

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val LocalTVPadding = compositionLocalOf { PaddingValues(0.dp) }

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