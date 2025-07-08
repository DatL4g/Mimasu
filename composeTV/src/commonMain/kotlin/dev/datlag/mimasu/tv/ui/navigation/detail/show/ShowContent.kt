package dev.datlag.mimasu.tv.ui.navigation.detail.show

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import coil3.compose.AsyncImage
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_show_episodes_count
import dev.datlag.mimasu.tv.ui.navigation.detail.show.components.EpisodeItem
import dev.datlag.mimasu.tv.ui.navigation.detail.show.components.ShowDrawerContent
import dev.datlag.mimasu.tv.ui.navigation.detail.show.components.ShowPosterContent
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.tooling.compose.ifTrue
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ShowContent(
    show: Show?,
    initial: TV?,
    showSeason: Show.Season?,
    seasonState: ShowViewModel.SeasonState,
    onSelectSeason: (Show.Season) -> Unit
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
                    modifier = Modifier.fillParentMaxWidth().fillParentMaxHeight(0.8F)
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
                    items(seasonState.season.episodes.toImmutableList()) { episode ->
                        EpisodeItem(
                            selected = selectedEpisode == episode,
                            episode = episode,
                            modifier = Modifier.fillParentMaxWidth().padding(horizontal = 32.dp),
                            markAsWatched = { },
                            markAsUnWatched = { }
                        )
                    }
                }
                else -> { }
            }
        }
    }
}