package dev.datlag.mimasu.ui.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.produceVirtualIOState
import org.kodein.di.compose.localDI

@Composable
actual fun rememberShowAvailability(
    show: Show?,
    initial: TV?
): ShowState = with(localDI()) {
    if (show == null && initial == null) return ShowState.Unavailable

    return produceVirtualIOState<ShowState>(initialValue = ShowState.Initializing) {
        value = ShowState.Unavailable
    }.value
}

@Composable
actual fun rememberEpisodeStream(
    showState: ShowState,
    tmdbId: Int?,
    seasonNumber: Int?,
    episode: Season.Episode,
): EpisodeStream = with(localDI()) {
    val state = remember(showState, tmdbId) {
        EpisodeStream(
            showState = showState,
            tmdbId = tmdbId
        )
    }

    return state
}