package dev.datlag.mimasu.tv.ui.navigation.detail.show

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import dev.datlag.mimasu.core.findAroundPositionOrNull
import dev.datlag.mimasu.firebase.firestore.ShowData
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tv.ui.navigation.detail.show.components.EpisodeItem
import dev.datlag.mimasu.tv.ui.navigation.detail.show.components.ShowDrawerContent
import dev.datlag.mimasu.tv.ui.navigation.detail.show.components.ShowPosterContent
import dev.datlag.mimasu.ui.other.ShowState
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.tooling.compose.ifTrue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun ShowContent(
    show: Show?,
    initial: TV?,
    showSeason: Show.Season?,
    seasonState: ShowViewModel.SeasonState,
    showAvailability: ShowState,
    episodesData: ImmutableList<ShowData.EpisodeData>,
    loggedIn: Boolean,
    onSelectSeason: (Show.Season) -> Unit,
    onStream: (VideoViewModel.WatchType.Show) -> Unit,
    markAsWatched: suspend (Season.Episode) -> Unit,
    markAsUnWatched: suspend (Season.Episode) -> Unit,
    onLogin: () -> Unit
) {
    var selectedEpisode by remember(seasonState) { mutableStateOf<Season.Episode?>(null) }
    val drawerFocus = remember { FocusRequester() }
    val contentFocus = remember { FocusRequester() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        modifier = Modifier.fillMaxHeight(),
        drawerState = drawerState,
        drawerContent = {
            ShowDrawerContent(
                show = show,
                season = showSeason,
                contentFocus = contentFocus,
                modifier = Modifier
                    .fillMaxHeight()
                    .ifTrue(drawerState.currentValue == DrawerValue.Open) {
                        background(MaterialTheme.colorScheme.background)
                    }
                    .focusRequester(drawerFocus),
                onSelect = onSelectSeason
            )
        }
    ) {
        val listState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = NavigationDrawerItemDefaults.CollapsedDrawerItemWidth + 8.dp)
                .focusRequester(contentFocus)
                .focusProperties {
                    start = drawerFocus
                },
            state = listState
        ) {
            item {
                ShowPosterContent(
                    show = show,
                    initial = initial,
                    listState = listState,
                    loggedIn = loggedIn,
                    modifier = Modifier.fillParentMaxWidth().fillParentMaxHeight(0.8F),
                    onLogin = onLogin
                )
            }
            item {
                val overview = remember(show?.overview, initial?.overview) {
                    show?.overview?.ifBlank { null } ?: initial?.overview?.ifBlank { null }
                }

                overview?.let {
                    Text(
                        modifier = Modifier.fillParentMaxWidth().padding(32.dp),
                        text = it,
                        softWrap = true
                    )
                }
            }
            when (seasonState) {
                is ShowViewModel.SeasonState.Success -> {
                    items(
                        items = seasonState.season.episodes.toImmutableList(),
                        key = { it.identifier }
                    ) { episode ->
                        val watchData = remember(show, episode) {
                            show?.let {
                                VideoViewModel.WatchType.Show(
                                    showInfo = show,
                                    seasonInfo = seasonState.season,
                                    episodeInfo = episode,
                                    sources = persistentMapOf()
                                )
                            }
                        }
                        val episodeData = remember(episodesData, episode.episodeNumber) {
                            episodesData.findAroundPositionOrNull(episode.episodeNumber) { it.number }
                        }

                        EpisodeItem(
                            selected = selectedEpisode == episode,
                            tmdbId = show?.id?.takeIf { it > 0 } ?: initial?.id,
                            episode = episode,
                            episodeData = episodeData,
                            seasonNumber = seasonState.season.seasonNumber,
                            showAvailability = showAvailability,
                            loggedIn = loggedIn,
                            modifier = Modifier.fillParentMaxWidth().padding(horizontal = 32.dp),
                            onStream = {
                                watchData?.copy(
                                    sources = it.sources.map { (k, v) ->
                                        VideoViewModel.SourceInfo(
                                            sourceTitle = k.sourceTitle,
                                            sourceLocale = k.sourceLocale,
                                            locale = k.locale
                                        ) to v
                                    }.toMap()
                                )?.let { s -> onStream(s)}
                            },
                            markAsWatched = {
                                markAsWatched(episode)
                            },
                            markAsUnWatched = {
                                markAsUnWatched(episode)
                            },
                            onLogin = onLogin
                        )
                    }
                }
                else -> { }
            }
        }
    }
}