package dev.datlag.mimasu.tv.ui.navigation.detail.movie

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tv.ui.navigation.detail.movie.components.MoviePosterContent
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie
import androidx.compose.ui.unit.dp

@Composable
internal fun MovieContent(
    movie: Movie?,
    initial: CommonMovie?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            MoviePosterContent(
                movie = movie,
                initial = initial,
                modifier = Modifier.fillParentMaxWidth().fillParentMaxHeight(0.8F)
            )
        }
        item {
            val overview = remember(movie?.overview, initial?.overview) {
                movie?.overview?.ifBlank { null } ?: initial?.overview?.ifBlank { null }
            }

            overview?.let {
                Text(
                    modifier = Modifier.fillParentMaxWidth().padding(32.dp),
                    text = it,
                    softWrap = true
                )
            }
        }
    }
}