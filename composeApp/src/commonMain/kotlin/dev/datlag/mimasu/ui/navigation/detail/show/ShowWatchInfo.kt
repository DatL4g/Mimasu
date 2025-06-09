package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import kotlinx.coroutines.flow.StateFlow
import dev.datlag.mimasu.extension.model.Show as Extension

@Composable
expect fun rememberShowAvailability(show: Show?, initial: TV?): Boolean

@Composable
expect fun rememberEpisodeStreamState(
    tmdbId: Int?,
    seasonNumber: Int?,
    episode: Season.Episode,
): EpisodeStreamState?

expect class EpisodeStreamState {
    val available: StateFlow<Boolean>

    suspend fun getStream(): Extension.Response?
}