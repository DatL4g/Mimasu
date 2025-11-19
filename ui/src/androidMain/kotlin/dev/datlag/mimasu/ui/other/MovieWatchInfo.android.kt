package dev.datlag.mimasu.ui.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.extension.MovieProvider
import dev.datlag.mimasu.extension.MovieProviderAndroid
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.produceVirtualIOState
import dev.datlag.tooling.compose.LaunchedVirtualIO
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull
import dev.datlag.mimasu.tmdb.model.Movie as IMovie
import dev.datlag.mimasu.extension.model.Movie as Extension

@Composable
actual fun rememberMovieAvailability(
    movie: Movie?,
    initial: IMovie?
): MovieStream = with(localDI()) {
    if (movie == null && initial == null) return MovieStream(
        state = MovieState.Unavailable,
        provider = null,
        tmdbId = null
    )

    val context = LocalContext.current
    val singletonProvider by instanceOrNull<MovieProvider>()
    val movieProvider = singletonProvider ?: remember(context) {
        ExtensionInitializer.getMovieProvider(context)
    }

    LaunchedVirtualIO(Unit) {
        (movieProvider as? MovieProviderAndroid)?.rebindIfNoneAvailable(context)
    }

    val tmdbId = remember(movie?.id, initial?.id) {
        movie?.id?.takeIf { it > 0 } ?: initial?.id
    }
    val request = remember(movie, initial, tmdbId) {
        Extension.Request(
            tmdbId = tmdbId,
            imdbId = movie?.imdbId?.ifBlank { null },
            wikidataId = movie?.externalIDs?.wikidataId?.ifBlank { null },
            title = movie?.title?.ifBlank { null } ?: initial?.title?.ifBlank { null },
            originalTitle = movie?.originalTitle?.ifBlank { null } ?: initial?.originalTitle?.ifBlank { null },
            firstReleaseYear = movie?.releaseLocalDate?.year ?: initial?.releaseLocalDate?.year,
            isAnimation = movie?.genres?.any { it.id == 16 } ?: initial?.genreIds?.any { it == 16 },
            appLocale = Locale.current.toLanguageTag()
        )
    }

    val state by produceVirtualIOState<MovieState>(initialValue = MovieState.Initializing, request) {
        val anyWatchProvider = movieProvider.requestId(request)

        value = MovieState.Available(anyWatchProvider)
    }

    return remember(state, movieProvider, tmdbId) {
        MovieStream(
            state = state,
            provider = movieProvider,
            tmdbId = tmdbId
        )
    }
}