package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.feature_temporarily_disabled
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieCast
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieCrew
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieGenres
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieInfo
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieOverview
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MoviePosterContent
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieProduction
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieTrailer
import io.tolgee.stringResource
import kotlinx.coroutines.launch
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@Composable
fun MovieContent(
    hazeState: HazeState,
    listState: LazyListState,
    snackbarState: SnackbarHostState,
    movie: Movie,
    initial: CommonMovie?,
    padding: PaddingValues,
    onCastClick: (Movie.Credits.Cast) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .hazeSource(state = hazeState),
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
            MovieProduction(
                movie = movie,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            MovieTrailer(
                url = movie.youtubeTrailer(
                    language = Locale.current.language,
                    country = Locale.current.region
                )?.key,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            val scope = rememberCoroutineScope()
            val disabledText = stringResource(Res.string.feature_temporarily_disabled)

            MovieCast(
                movie = movie,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {
                    scope.launch {
                        snackbarState.showSnackbar(disabledText, withDismissAction = true)
                    }
                }
            )
        }
        item {
            val scope = rememberCoroutineScope()
            val disabledText = stringResource(Res.string.feature_temporarily_disabled)

            MovieCrew(
                movie = movie,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {
                    scope.launch {
                        snackbarState.showSnackbar(disabledText, withDismissAction = true)
                    }
                }
            )
        }
    }
}