package dev.datlag.mimasu.tv.ui.navigation.movies

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_movies_now_playing
import dev.datlag.mimasu.tv.tv_movies_popular
import dev.datlag.mimasu.tv.tv_movies_top_rated
import dev.datlag.mimasu.tv.tv_movies_upcoming
import dev.datlag.mimasu.tv.ui.custom.MoviesSection
import dev.datlag.mimasu.ui.common.plus
import dev.datlag.mimasu.ui.viewmodel.MovieListsViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import io.tolgee.stringResource

@Composable
fun Movies(
    paddingValues: PaddingValues,
    onMovieClicked: (Movie) -> Unit
) {
    val movieViewModel = kodeinViewModel<MovieListsViewModel>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues.plus(PaddingValues(top = 32.dp))
    ) {
        item {
            MoviesSection(
                flow = movieViewModel.nowPlaying,
                title = stringResource(Res.string.tv_movies_now_playing),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onMovieClicked
            )
        }
        item {
            MoviesSection(
                flow = movieViewModel.upcoming,
                title = stringResource(Res.string.tv_movies_upcoming),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onMovieClicked
            )
        }
        item {
            MoviesSection(
                flow = movieViewModel.popular,
                title = stringResource(Res.string.tv_movies_popular),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onMovieClicked
            )
        }
        item {
            MoviesSection(
                flow = movieViewModel.topRated,
                title = stringResource(Res.string.tv_movies_top_rated),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onMovieClicked
            )
        }
    }
}