package dev.datlag.mimasu.ui.navigation.movies

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movies_now_playing
import dev.datlag.mimasu.composeapp.generated.resources.movies_popular
import dev.datlag.mimasu.composeapp.generated.resources.movies_top_rated
import dev.datlag.mimasu.composeapp.generated.resources.movies_upcoming
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.MaterialSymbols.invoke
import dev.datlag.mimasu.ui.custom.MovieCard
import dev.datlag.mimasu.ui.custom.ScrollIconButton
import dev.datlag.mimasu.ui.viewmodel.MovieListsViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun Movies(
    onMovieClicked: (Movie) -> Unit
) {
    val movieListsViewModel = kodeinViewModel<MovieListsViewModel>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = WindowInsets.statusBars.asPaddingValues()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val nowPlaying = movieListsViewModel.nowPlaying.collectAsLazyPagingItems()
                val listState = rememberLazyListState()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.movies_now_playing),
                        style = Platform.typography().headlineSmall,
                        maxLines = 1
                    )
                    if (!Platform.isAndroid) {
                        Spacer(modifier = Modifier.weight(1F))
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.Start,
                            maxScrollItem = nowPlaying.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                contentDescription = null
                            )
                        }
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.End,
                            maxScrollItem = nowPlaying.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_RIGHT,
                                contentDescription = null
                            )
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    state = listState
                ) {
                    items(nowPlaying.itemCount) { index ->
                        val movie = nowPlaying[index]

                        MovieCard(movie, onClick = onMovieClicked)
                    }
                    when {
                        nowPlaying.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                MovieCard(movie = null)
                            }
                        }
                        nowPlaying.loadState.append is LoadState.Loading -> {
                            items(3) {
                                MovieCard(movie = null)
                            }
                        }
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val upcoming = movieListsViewModel.upcoming.collectAsLazyPagingItems()
                val listState = rememberLazyListState()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.movies_upcoming),
                        style = Platform.typography().headlineSmall,
                        maxLines = 1
                    )
                    if (!Platform.isAndroid) {
                        Spacer(modifier = Modifier.weight(1F))
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.Start,
                            maxScrollItem = upcoming.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                contentDescription = null
                            )
                        }
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.End,
                            maxScrollItem = upcoming.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_RIGHT,
                                contentDescription = null
                            )
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    state = listState
                ) {
                    items(upcoming.itemCount) { index ->
                        val movie = upcoming[index]

                        MovieCard(movie, onClick = onMovieClicked)
                    }
                    when {
                        upcoming.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                MovieCard(movie = null)
                            }
                        }
                        upcoming.loadState.append is LoadState.Loading -> {
                            items(3) {
                                MovieCard(movie = null)
                            }
                        }
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val popular = movieListsViewModel.popular.collectAsLazyPagingItems()
                val listState = rememberLazyListState()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.movies_popular),
                        style = Platform.typography().headlineSmall,
                        maxLines = 1
                    )
                    if (!Platform.isAndroid) {
                        Spacer(modifier = Modifier.weight(1F))
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.Start,
                            maxScrollItem = popular.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                contentDescription = null
                            )
                        }
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.End,
                            maxScrollItem = popular.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_RIGHT,
                                contentDescription = null
                            )
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    state = listState
                ) {
                    items(popular.itemCount) { index ->
                        val movie = popular[index]

                        MovieCard(movie, onClick = onMovieClicked)
                    }
                    when {
                        popular.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                MovieCard(movie = null)
                            }
                        }
                        popular.loadState.append is LoadState.Loading -> {
                            items(3) {
                                MovieCard(movie = null)
                            }
                        }
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val topRated = movieListsViewModel.topRated.collectAsLazyPagingItems()
                val listState = rememberLazyListState()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.movies_top_rated),
                        style = Platform.typography().headlineSmall,
                        maxLines = 1
                    )
                    if (!Platform.isAndroid) {
                        Spacer(modifier = Modifier.weight(1F))
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.Start,
                            maxScrollItem = topRated.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                contentDescription = null
                            )
                        }
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.End,
                            maxScrollItem = topRated.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_RIGHT,
                                contentDescription = null
                            )
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    state = listState
                ) {
                    items(topRated.itemCount) { index ->
                        val movie = topRated[index]

                        MovieCard(movie, onClick = onMovieClicked)
                    }
                    when {
                        topRated.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                MovieCard(movie = null)
                            }
                        }
                        topRated.loadState.append is LoadState.Loading -> {
                            items(3) {
                                MovieCard(movie = null)
                            }
                        }
                    }
                }
            }
        }
    }
}