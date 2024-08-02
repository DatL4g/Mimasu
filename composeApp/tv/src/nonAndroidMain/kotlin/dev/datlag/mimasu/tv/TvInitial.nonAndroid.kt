package dev.datlag.mimasu.tv

import androidx.compose.runtime.Composable

@Composable
actual fun TvInitial(
    profile: TvInitialNavigation,
    home: TvInitialNavigation,
    favorites: TvInitialNavigation,
    search: TvInitialSearch,
    content: @Composable () -> Unit
) = content()