package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.extension.model.Show as Extension

@Composable
expect fun rememberShowAvailability(show: Show?, initial: TV?): Boolean

@Composable
expect fun rememberEpisodeWatchInfo(
    tmdbId: Int?,
    seasonNumber: Int?,
    episode: Season.Episode,
): Boolean