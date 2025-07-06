package dev.datlag.mimasu.tv.ui.navigation.detail.movie

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel

@Composable
internal fun MovieDetail() {
    val movieViewModel = kodeinViewModel<MovieViewModel>()
    val movieState by movieViewModel.movie.collectAsStateWithLifecycle(MovieViewModel.State.Loading)
    val initial by movieViewModel.initialMovie.collectAsStateWithLifecycle()

    when (val current = movieState) {
        is MovieViewModel.State.Error -> {
            ErrorState(
                throwable = current.throwable,
                additionalInfo = "[TV] MovieDetail",
                modifier = Modifier.fillMaxSize()
            )
        }
        is MovieViewModel.State.Loading, is MovieViewModel.State.Success -> {
            MovieContent(
                movie = current.getOrNull(),
                initial = initial
            )
        }
    }
}