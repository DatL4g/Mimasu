package dev.datlag.mimasu.ui.other

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show

@Composable
expect fun rememberShowAvailability(show: Show?, initial: TV?): ShowState

@Composable
expect fun rememberEpisodeStream(
    showState: ShowState,
    tmdbId: Int?,
    seasonNumber: Int?,
    episode: Season.Episode,
): EpisodeStream
