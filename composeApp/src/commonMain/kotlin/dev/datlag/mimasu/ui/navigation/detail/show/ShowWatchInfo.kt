package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import dev.datlag.mimasu.extension.model.Show as Extension

@Composable
expect fun rememberShowAvailability(show: Show?, initial: TV?): ShowState

@Composable
expect fun rememberEpisodeStream(
    showState: ShowState,
    tmdbId: Int?,
    seasonNumber: Int?,
    episode: Season.Episode,
): EpisodeStream

expect class EpisodeStream {
    val state: StateFlow<EpisodeStreamState>

    suspend fun getStream(): Extension.Response?
}

@Serializable
sealed interface ShowState {

    @Serializable
    data object Initializing : ShowState

    @Serializable
    data class Available(val state: Boolean) : ShowState

    @Serializable
    data object Unavailable : ShowState
}

@Serializable
sealed interface EpisodeStreamState {

    @Serializable
    data object Initializing : EpisodeStreamState

    @Serializable
    data object Requesting : EpisodeStreamState

    @Serializable
    data class Available(val state: Boolean) : EpisodeStreamState

    @Serializable
    data object Unavailable : EpisodeStreamState
}