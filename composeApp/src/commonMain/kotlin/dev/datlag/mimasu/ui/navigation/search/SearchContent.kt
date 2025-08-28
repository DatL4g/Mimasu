package dev.datlag.mimasu.ui.navigation.search

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.search_movies
import dev.datlag.mimasu.composeapp.generated.resources.search_people
import dev.datlag.mimasu.composeapp.generated.resources.search_series
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.TV
import androidx.paging.compose.LazyPagingItems
import dev.datlag.mimasu.ui.common.plus
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.MovieCard
import dev.datlag.mimasu.ui.custom.PersonCard
import dev.datlag.mimasu.ui.custom.ScrollIconButton
import dev.datlag.mimasu.ui.custom.ShowCard
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource

@Composable
fun SearchContent(
    padding: PaddingValues,
    people: LazyPagingItems<People>,
    movies: LazyPagingItems<Movie>,
    tv: LazyPagingItems<TV>,
    onPersonClicked: (People) -> Unit,
    onMovieClicked: (Movie) -> Unit,
    onShowClicked: (TV) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = padding + PaddingValues(top = 16.dp)
    ) {
        if (people.itemCount > 0 || people.loadState.refresh is LoadState.Loading) {
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .animateContentSize()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val listState = rememberLazyListState()

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.search_people),
                            style = Platform.typography().headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        if (!Platform.isAndroid) {
                            Spacer(modifier = Modifier.weight(1F))
                            ScrollIconButton(
                                listState = listState,
                                alignment = Alignment.Start,
                                maxScrollItem = people.itemCount
                            ) {
                                MaterialSymbols(
                                    name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                    contentDescription = null
                                )
                            }
                            ScrollIconButton(
                                listState = listState,
                                alignment = Alignment.End,
                                maxScrollItem = people.itemCount
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
                        items(people.itemCount) { index ->
                            PersonCard(
                                person = people[index],
                                onClick = onPersonClicked
                            )
                        }
                        when {
                            people.loadState.refresh is LoadState.Loading -> {
                                items(5) {
                                    PersonCard(person = null)
                                }
                            }
                            people.loadState.append is LoadState.Loading -> {
                                items(3) {
                                    PersonCard(person = null)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (tv.itemCount > 0 || tv.loadState.refresh is LoadState.Loading) {
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .animateContentSize()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val listState = rememberLazyListState()

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.search_series),
                            style = Platform.typography().headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        if (!Platform.isAndroid) {
                            Spacer(modifier = Modifier.weight(1F))
                            ScrollIconButton(
                                listState = listState,
                                alignment = Alignment.Start,
                                maxScrollItem = tv.itemCount
                            ) {
                                MaterialSymbols(
                                    name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                    contentDescription = null
                                )
                            }
                            ScrollIconButton(
                                listState = listState,
                                alignment = Alignment.End,
                                maxScrollItem = tv.itemCount
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
                        items(tv.itemCount) { index ->
                            ShowCard(
                                tv = tv[index],
                                onClick = onShowClicked
                            )
                        }
                        when {
                            tv.loadState.refresh is LoadState.Loading -> {
                                items(5) {
                                    ShowCard(tv = null)
                                }
                            }
                            tv.loadState.append is LoadState.Loading -> {
                                items(3) {
                                    ShowCard(tv = null)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (movies.itemCount > 0 || movies.loadState.refresh is LoadState.Loading) {
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .animateContentSize()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val listState = rememberLazyListState()

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.search_movies),
                            style = Platform.typography().headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        if (!Platform.isAndroid) {
                            Spacer(modifier = Modifier.weight(1F))
                            ScrollIconButton(
                                listState = listState,
                                alignment = Alignment.Start,
                                maxScrollItem = movies.itemCount
                            ) {
                                MaterialSymbols(
                                    name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                    contentDescription = null
                                )
                            }
                            ScrollIconButton(
                                listState = listState,
                                alignment = Alignment.End,
                                maxScrollItem = movies.itemCount
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
                        items(movies.itemCount) { index ->
                            MovieCard(
                                movie = movies[index],
                                onClick = onMovieClicked
                            )
                        }
                        when {
                            movies.loadState.refresh is LoadState.Loading -> {
                                items(5) {
                                    MovieCard(movie = null)
                                }
                            }
                            movies.loadState.append is LoadState.Loading -> {
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
}