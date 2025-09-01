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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.datlag.mimasu.core.findAroundPositionOrNull
import dev.datlag.mimasu.firebase.firestore.ShowData
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.navigation.detail.show.components.EpisodeDialog
import dev.datlag.mimasu.ui.navigation.detail.show.components.EpisodeItem
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowGenres
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowInfo
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowOverview
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowPosterContent
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowProduction
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowSeason
import dev.datlag.mimasu.ui.other.ShowState
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.tooling.compose.LaunchedDefault
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

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
    episodesData: ImmutableList<ShowData.EpisodeData>,
    loggedIn: Boolean,
    onSelectSeason: (Show.Season) -> Unit = {},
    onStream: (VideoViewModel.WatchType.Show) -> Unit,
    markAsWatched: suspend (Season.Episode) -> Unit,
    markAsUnWatched: suspend (Season.Episode) -> Unit,
    onDiscover: (Int) -> Unit,
    onLogin: () -> Unit
) {
    var episodeDialogVisible by remember { mutableStateOf(false) }
    var episodeClickBlocked by remember { mutableStateOf(false) }

    LaunchedDefault(episodeClickBlocked) {
        if (episodeClickBlocked) {
            delay(10.seconds)
            episodeClickBlocked = false
        }
    }

    if (episodeDialogVisible) {
        EpisodeDialog(
            onDismiss = {
                episodeDialogVisible = false
            }
        )
    }

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
                    .padding(bottom = 16.dp),
                onClick = onDiscover
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
                items(
                    items = seasonState.season.episodes.toImmutableList(),
                    key = { it.identifier }
                ) { episode ->
                    val watchData = remember(show, episode) {
                        VideoViewModel.WatchType.Show(
                            showInfo = show,
                            seasonInfo = seasonState.season,
                            episodeInfo = episode,
                            sources = persistentMapOf()
                        )
                    }
                    val episodeData = remember(episodesData, episode.episodeNumber) {
                        episodesData.findAroundPositionOrNull(episode.episodeNumber) { it.number }
                    }

                    EpisodeItem(
                        tmdbId = show.id.takeIf { it > 0 } ?: initial?.id,
                        episode = episode,
                        episodeData = episodeData,
                        seasonNumber = seasonState.season.seasonNumber,
                        showAvailability = showAvailability,
                        loggedIn = loggedIn,
                        clickBlocked = episodeClickBlocked,
                        modifier = Modifier.fillParentMaxWidth().padding(4.dp),
                        onDialog = {
                            episodeDialogVisible = true
                        },
                        onStream = {
                            onStream(watchData.copy(
                                sources = it.sources.map { (k, v) ->
                                    VideoViewModel.SourceInfo(
                                        sourceTitle = k.sourceTitle,
                                        sourceLocale = k.sourceLocale,
                                        locale = k.locale
                                    ) to v
                                }.toMap()
                            ))
                        },
                        markAsWatched = {
                            markAsWatched(episode)
                        },
                        markAsUnWatched = {
                            markAsUnWatched(episode)
                        },
                        onLogin = onLogin,
                        blockClick = {
                            episodeClickBlocked = it
                        }
                    )
                }
            }
        }
    }
}