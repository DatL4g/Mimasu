package dev.datlag.mimasu.ui.navigation.search

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.common.plus
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.search_movies
import dev.datlag.mimasu.composeapp.generated.resources.search_people
import dev.datlag.mimasu.composeapp.generated.resources.search_series
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.repository.SearchRepository
import dev.datlag.mimasu.ui.custom.MovieCard
import dev.datlag.mimasu.ui.custom.PersonCard
import dev.datlag.mimasu.ui.custom.ShowCard
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchContent(
    padding: PaddingValues,
    query: String?,
    result: SearchRepository.SearchResult,
    onShowClicked: (TV) -> Unit,
    onMovieClicked: (Movie) -> Unit
) {
    val loading = remember(result) { result is SearchRepository.SearchResult.Loading }
    val success = remember(result) { result as? SearchRepository.SearchResult.Success }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = padding + PaddingValues(top = 16.dp)
    ) {
        if (loading || success?.hasPeople() == true) {
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .animateContentSize()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(Res.string.search_people),
                        style = Platform.typography().headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        if (success != null) {
                            items(success.people.toImmutableList()) {
                                PersonCard(
                                    person = it,
                                    placeholder = false
                                )
                            }
                        } else {
                            items(5) {
                                PersonCard(
                                    person = null,
                                    placeholder = true
                                )
                            }
                        }
                    }
                }
            }
        }
        if (loading || success?.hasSeries() == true) {
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .animateContentSize()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(Res.string.search_series),
                        style = Platform.typography().headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        if (success != null) {
                            items(success.series.toImmutableList()) {
                                ShowCard(
                                    tv = it,
                                    onClick = onShowClicked
                                )
                            }
                        } else {
                            items(5) {
                                ShowCard(tv = null)
                            }
                        }
                    }
                }
            }
        }
        if (loading || success?.hasMovies() == true) {
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .animateContentSize()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(Res.string.search_movies),
                        style = Platform.typography().headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        if (success != null) {
                            items(success.movies.toImmutableList()) {
                                MovieCard(
                                    movie = it,
                                    onClick = onMovieClicked
                                )
                            }
                        } else {
                            items(5) {
                                MovieCard(movie = null)
                            }
                        }
                    }
                }
            }
        }
    }
}