package dev.datlag.mimasu.tv.ui.navigation.detail.show

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.other.rememberShowAvailability
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun ShowDetail(
    onStream: (VideoViewModel.WatchType.Show) -> Unit,
    onLogin: () -> Unit
) {
    val showViewModel = kodeinViewModel<ShowViewModel>()
    val accountViewModel = accountViewModel()
    val user by accountViewModel.user.collectAsState()
    val showState by showViewModel.show.collectAsState(ShowViewModel.ShowState.Loading)
    val initial by showViewModel.initialShow.collectAsState()
    val showSeason by showViewModel.showSeason.collectAsState()
    val initialSeasonState = remember(showSeason) {
        if (showSeason == null) {
            ShowViewModel.SeasonState.Empty
        } else {
            ShowViewModel.SeasonState.Loading
        }
    }
    val seasonState by showViewModel.season.collectAsState(initialSeasonState)
    val showAvailability = rememberShowAvailability(
        show = showState.getOrNull(),
        initial = initial
    )
    val episodeData by showViewModel.episodesData.collectAsState(null)

    when (val current = showState) {
        is ShowViewModel.ShowState.Error -> {
            ErrorState(
                throwable = current.throwable,
                additionalInfo = "[TV] ShowDetail",
                modifier = Modifier.fillMaxSize()
            )
        }
        is ShowViewModel.ShowState.Loading, is ShowViewModel.ShowState.Success -> {
            ShowContent(
                show = current.getOrNull(),
                initial = initial,
                showSeason = showSeason,
                seasonState = seasonState,
                showAvailability = showAvailability,
                episodesData = episodeData.orEmpty().toImmutableList(),
                loggedIn = user != null,
                onSelectSeason = {
                    showViewModel.select(it)
                },
                onStream = onStream,
                markAsWatched = {
                    val seasonNumber = seasonState.getOrNull()?.seasonNumber ?: showSeason?.seasonNumber
                    val showId = current.getOrNull()?.id ?: initial?.id

                    if (seasonNumber != null && showId != null) {
                        showViewModel.markAsWatched(
                            tmdbId = showId,
                            seasonNumber = seasonNumber,
                            episode = it
                        )
                    }
                },
                markAsUnWatched = {
                    val seasonNumber = seasonState.getOrNull()?.seasonNumber ?: showSeason?.seasonNumber
                    val showId = current.getOrNull()?.id ?: initial?.id

                    if (seasonNumber != null && showId != null) {
                        showViewModel.markAsUnwatched(
                            tmdbId = showId,
                            seasonNumber = seasonNumber,
                            episode = it
                        )
                    }
                },
                onLogin = onLogin
            )
        }
    }
}