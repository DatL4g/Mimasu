package dev.datlag.mimasu.tv.ui.navigation.home

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_home_trending_movies_today
import dev.datlag.mimasu.tv.tv_home_trending_movies_weekly
import dev.datlag.mimasu.tv.tv_home_trending_series_today
import dev.datlag.mimasu.tv.tv_home_trending_series_weekly
import dev.datlag.mimasu.tv.ui.custom.MoviesSection
import dev.datlag.mimasu.tv.ui.custom.SeriesSection
import dev.datlag.mimasu.tv.ui.navigation.home.components.BookmarkedMovies
import dev.datlag.mimasu.tv.ui.navigation.home.components.BookmarkedShows
import dev.datlag.mimasu.ui.viewmodel.TrendingViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import io.tolgee.stringResource

@Composable
fun Home(
    paddingValues: PaddingValues,
    onMovieClicked: (Movie) -> Unit,
    onShowClicked: (TV) -> Unit
) {
    val trendingViewModel = kodeinViewModel<TrendingViewModel>()
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        item {
            BookmarkedShows(
                paddingValues = paddingValues,
                listState = listState,
                onClick = {
                    onShowClicked(it.asCommon())
                }
            )
        }
        item {
            BookmarkedMovies(
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = {
                    onMovieClicked(it.asCommon())
                }
            )
        }
        item {
            SeriesSection(
                flow = trendingViewModel.dayTV,
                title = stringResource(Res.string.tv_home_trending_series_today),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onShowClicked
            )
        }
        item {
            MoviesSection(
                flow = trendingViewModel.dayMovies,
                title = stringResource(Res.string.tv_home_trending_movies_today),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onMovieClicked
            )
        }
        item {
            SeriesSection(
                flow = trendingViewModel.weekTV,
                title = stringResource(Res.string.tv_home_trending_series_weekly),
                orientation = Orientation.Vertical,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onShowClicked
            )
        }
        item {
            MoviesSection(
                flow = trendingViewModel.weekMovies,
                title = stringResource(Res.string.tv_home_trending_movies_weekly),
                orientation = Orientation.Vertical,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onMovieClicked
            )
        }
    }
}