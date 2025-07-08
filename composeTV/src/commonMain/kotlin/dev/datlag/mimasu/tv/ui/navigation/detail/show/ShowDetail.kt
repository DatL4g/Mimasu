package dev.datlag.mimasu.tv.ui.navigation.detail.show

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun ShowDetail() {
    val showViewModel = kodeinViewModel<ShowViewModel>()
    val showState by showViewModel.show.collectAsStateWithLifecycle(ShowViewModel.ShowState.Loading)
    val initial by showViewModel.initialShow.collectAsStateWithLifecycle()
    val showSeason by showViewModel.showSeason.collectAsStateWithLifecycle()
    val initialSeasonState = remember(showSeason) {
        if (showSeason == null) {
            ShowViewModel.SeasonState.Empty
        } else {
            ShowViewModel.SeasonState.Loading
        }
    }
    val seasonState by showViewModel.season.collectAsStateWithLifecycle(initialSeasonState)
    val episodeData by showViewModel.episodesData.collectAsStateWithLifecycle(null)

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
                episodesData = episodeData.orEmpty().toImmutableList(),
                onSelectSeason = {
                    showViewModel.select(it)
                },
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
                }
            )
        }
    }
}