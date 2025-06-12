package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.navigation.detail.show.components.EpisodeItem
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowGenres
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowInfo
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowOverview
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowPosterContent
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowProduction
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowSeason
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.tooling.compose.ifTrue
import kotlinx.collections.immutable.toImmutableList
import dev.datlag.mimasu.extension.model.Show as Extension

@Composable
fun ShowContent(
    hazeState: HazeState,
    listState: LazyListState,
    show: Show,
    showSeason: Show.Season?,
    seasonState: ShowViewModel.SeasonState,
    initial: TV?,
    showAvailability: ShowState,
    padding: PaddingValues,
    onSelectSeason: (Show.Season) -> Unit = {},
    onStream: (Extension.Response) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .hazeSource(state = hazeState),
        contentPadding = padding
    ) {
        item {
            ShowPosterContent(
                show = show,
                initial = initial,
                season = showSeason,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(16.dp)
            )
        }
        item {
            ShowInfo(
                show = show,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            ShowGenres(
                show = show,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
        item {
            ShowSeason(
                show = show,
                season = showSeason,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                onSelect = onSelectSeason
            )
        }
        item {
            ShowOverview(
                show = show,
                initial = initial,
                season = showSeason,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            ShowProduction(
                show = show,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        when (seasonState) {
            is ShowViewModel.SeasonState.Empty -> { }
            is ShowViewModel.SeasonState.Loading -> item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.5F).clip(CircleShape)
                    )
                }
            }
            is ShowViewModel.SeasonState.Error -> item {
                ErrorState(
                    throwable = seasonState.throwable,
                    additionalInfo = "ShowContent [SeasonState]",
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
            is ShowViewModel.SeasonState.Success -> {
                itemsIndexed(seasonState.season.episodes.toImmutableList()) { index, episode ->
                    EpisodeItem(
                        tmdbId = show.id.takeIf { it > 0 } ?: initial?.id,
                        episode = episode,
                        seasonNumber = seasonState.season.seasonNumber,
                        showAvailability = showAvailability,
                        modifier = Modifier.fillParentMaxWidth().ifTrue(index >= seasonState.season.episodes.size - 1) {
                            padding(bottom = 16.dp)
                        },
                        onStream = onStream
                    )
                }
            }
        }
    }
}