package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieCredits
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieGenres
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieInfo
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieOverview
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MoviePosterContent
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@Composable
fun MovieContent(
    movie: Movie,
    initial: CommonMovie?,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = padding
    ) {
        item {
            MoviePosterContent(
                movie = movie,
                initial = initial,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(16.dp)
            )
        }
        item {
            MovieInfo(
                movie = movie,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            MovieGenres(
                movie = movie,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
        item {
            MovieOverview(
                movie = movie,
                initial = initial,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            // Probably more info here
            // So FAB don't overlap with cast images -> hard to see
        }
        item {
            MovieCredits(
                movie = movie,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}