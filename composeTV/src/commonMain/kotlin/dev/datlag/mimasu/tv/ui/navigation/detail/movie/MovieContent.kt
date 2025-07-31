package dev.datlag.mimasu.tv.ui.navigation.detail.movie

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tv.ui.navigation.detail.movie.components.MovieCast
import dev.datlag.mimasu.tv.ui.navigation.detail.movie.components.MoviePosterContent
import kotlinx.collections.immutable.toImmutableSet
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@Composable
internal fun MovieContent(
    movie: Movie?,
    initial: CommonMovie?,
    loggedIn: Boolean,
    onLogin: () -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        item {
            MoviePosterContent(
                movie = movie,
                initial = initial,
                listState = listState,
                loggedIn = loggedIn,
                modifier = Modifier.fillParentMaxWidth().fillParentMaxHeight(0.8F),
                onLogin = onLogin
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
        item {
            val cast = remember(movie?.credits) { movie?.credits?.cast.orEmpty().toImmutableSet() }

            MovieCast(
                casting = cast,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}