package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.navigation.detail.show.components.EpisodeItem
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowGenres
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowInfo
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowOverview
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowPosterContent
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowProduction
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowSeason
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.compose.ifTrue
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ShowContent(
    hazeState: HazeState,
    listState: LazyListState,
    show: Show,
    initial: TV?,
    padding: PaddingValues,
    viewModel: ShowViewModel = kodeinViewModel<ShowViewModel>()
) {
    val showSeason by viewModel.showSeason.collectAsStateWithLifecycle()
    val initialSeasonState = remember(showSeason) {
        if (showSeason == null) {
            ShowViewModel.SeasonState.Empty
        } else {
            ShowViewModel.SeasonState.Loading
        }
    }
    val seasonState by viewModel.season.collectAsStateWithLifecycle(initialSeasonState)

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
                onSelect = {
                    viewModel.select(it)
                }
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
        when (val current = seasonState) {
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
                Text(text = "Loading Season failed: ${current.throwable}")
            }
            is ShowViewModel.SeasonState.Success -> {
                itemsIndexed(current.season.episodes.toImmutableList()) { index, episode ->
                    EpisodeItem(
                        episode = episode,
                        modifier = Modifier.fillParentMaxWidth().ifTrue(index >= current.season.episodes.size - 1) {
                            padding(bottom = 16.dp)
                        },
                    )
                }
            }
        }
    }
}