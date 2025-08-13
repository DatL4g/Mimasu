package dev.datlag.mimasu.tv.ui.navigation.search

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_search_info_default
import dev.datlag.mimasu.tv.tv_search_info_error
import dev.datlag.mimasu.tv.tv_search_start_typing
import dev.datlag.mimasu.tv.ui.custom.Keyboard
import dev.datlag.mimasu.tv.ui.custom.MovieCard
import dev.datlag.mimasu.tv.ui.custom.ShowCard
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.SearchInfo
import dev.datlag.mimasu.ui.viewmodel.SearchViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import io.tolgee.stringResource

@Composable
fun Search(
    paddingValues: PaddingValues,
    onMovieClicked: (Movie) -> Unit,
    onShowClicked: (TV) -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    Row(
        modifier = Modifier
            .padding(start = paddingValues.calculateStartPadding(layoutDirection) + 32.dp)
            .padding(end = paddingValues.calculateEndPadding(layoutDirection) + 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val searchViewModel = kodeinViewModel<SearchViewModel>()
        val query by searchViewModel.query.collectAsState()
        val multi = searchViewModel.showsAndMovies.collectAsLazyPagingItems()

        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding() + 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(280.dp),
                text = query?.trim() ?: stringResource(Res.string.tv_search_start_typing),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Keyboard(
                modifier = Modifier
                    .width(280.dp),
                value = query ?: "",
                onValueChange = {
                    searchViewModel.updateQuery(it)
                }
            )
        }
        when {
            multi.itemCount <= 0 && query.isNullOrEmpty() -> {
                SearchInfo(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxHeight()
                        .padding(top = paddingValues.calculateTopPadding() + 32.dp)
                        .padding(horizontal = 32.dp),
                    iconName = MaterialSymbols.SEARCH,
                    text = stringResource(Res.string.tv_search_info_default)
                )
            }
            multi.itemCount <= 0 && multi.loadState.hasError -> {
                SearchInfo(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxHeight()
                        .padding(top = paddingValues.calculateTopPadding() + 32.dp)
                        .padding(horizontal = 32.dp),
                    iconName = MaterialSymbols.ERROR,
                    iconTint = MaterialTheme.colorScheme.error,
                    text = stringResource(Res.string.tv_search_info_error)
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(120.dp),
                    modifier = Modifier.weight(1F).fillMaxHeight(),
                    contentPadding = PaddingValues(top = paddingValues.calculateTopPadding() + 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(multi.itemCount) { index ->
                        val item = multi[index]

                        when (item) {
                            is People -> { /* Should never happen, just for compiler */ }
                            is Movie -> {
                                MovieCard(
                                    movie = item,
                                    orientation = Orientation.Vertical,
                                    onClick = onMovieClicked
                                )
                            }
                            is TV -> {
                                ShowCard(
                                    tv = item,
                                    orientation = Orientation.Vertical,
                                    onClick = onShowClicked
                                )
                            }
                            null -> {
                                ShowCard(
                                    tv = null,
                                    orientation = Orientation.Vertical
                                )
                            }
                        }
                    }
                    when {
                        multi.loadState.refresh is LoadState.Loading -> {
                            items(8) {
                                ShowCard(
                                    tv = null,
                                    orientation = Orientation.Vertical
                                )
                            }
                        }
                        multi.loadState.append is LoadState.Loading -> {
                            items(5) {
                                ShowCard(
                                    tv = null,
                                    orientation = Orientation.Vertical
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}