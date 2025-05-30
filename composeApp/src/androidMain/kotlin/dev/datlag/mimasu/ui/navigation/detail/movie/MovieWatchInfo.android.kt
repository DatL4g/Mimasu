package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.extension.MovieProvider
import dev.datlag.mimasu.tmdb.model.details.Movie
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull
import dev.datlag.mimasu.extension.model.Movie as Extension

@Composable
actual fun rememberMovieWatchInfo(
    movie: Movie?,
    initial: dev.datlag.mimasu.tmdb.model.Movie?
): Extension.Response? = with(localDI()) {
    if (movie == null && initial == null) return null

    val context = LocalContext.current
    val singletonProvider by instanceOrNull<MovieProvider>()
    val movieProvider = singletonProvider ?: remember(context) {
        ExtensionInitializer.getMovieProvider(context)
    }
    val request = remember(movie, initial) {
        Extension.Request(
            tmdbId = movie?.id?.takeIf { it > 0 } ?: initial?.id,
            imdbId = movie?.imdbId?.ifBlank { null },
            wikidataId = movie?.externalIDs?.wikidataId?.ifBlank { null },
            title = movie?.title?.ifBlank { null } ?: initial?.title?.ifBlank { null },
            originalTitle = movie?.originalTitle?.ifBlank { null } ?: initial?.originalTitle?.ifBlank { null },
            runtimeInMinutes = movie?.runtime,
            releaseYear = movie?.releaseLocalDate?.year ?: initial?.releaseYear
        )
    }

    return produceState<Extension.Response?>(initialValue = null, request) {
        val allWatchInfo = movieProvider.requestInfo(request)

        value = allWatchInfo.firstOrNull()
    }.value
}