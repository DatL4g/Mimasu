package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.extension.movie.Request
import dev.datlag.mimasu.tmdb.model.details.Movie

@Composable
actual fun rememberMovieWatchInfo(
    movie: Movie?,
    initial: dev.datlag.mimasu.tmdb.model.Movie?
): MovieWatchInfo? {
    if (movie == null && initial == null) return null

    val context = LocalContext.current
    val movieProvider = remember(context) {
        ExtensionInitializer.getMovieProvider(context)
    }
    val tmdbId = remember(movie, initial) {
        movie?.id?.takeIf { it > 0 } ?: initial?.id ?: 0
    }
    val imdbId = remember(movie) {
        movie?.imdbId?.ifBlank { null }
    }
    val wikidataId = remember(movie) {
        movie?.externalIDs?.wikidataId?.ifBlank { null }
    }
    val title = remember(movie, initial) {
        movie?.title?.ifBlank { null } ?: initial?.title?.ifBlank { null }
    }
    val originalTitle = remember(movie, initial) {
        movie?.originalTitle?.ifBlank { null } ?: initial?.originalTitle?.ifBlank { null }
    }
    val runtime = remember(movie) {
        movie?.runtime ?: 0
    }

    return produceState<MovieWatchInfo?>(initialValue = null, tmdbId, imdbId, wikidataId, title, originalTitle, runtime) {
        val allWatchInfo = movieProvider.requestInfo(object : Request.Stub() {
            override fun getTmdbId(): Int {
                return tmdbId
            }

            override fun getImdbId(): String? {
                return imdbId
            }

            override fun getWikidataId(): String? {
                return wikidataId
            }

            override fun getTitle(): String? {
                return title
            }

            override fun getOriginalTitle(): String? {
                return originalTitle
            }

            override fun getRuntimeInMinutes(): Int {
                return runtime
            }
        })

        val grouped = allWatchInfo.flatMap { watchInfo ->
            val sources = watchInfo.sources
            watchInfo.languageSourceMapping.mapNotNull { (language, indexString) ->
                if (language.isNullOrBlank()) {
                    return@mapNotNull null
                }
                val index = indexString.toIntOrNull()?.takeIf { it in sources.indices } ?: return@mapNotNull null
                sources[index]?.let { language to it }
            }
        }.groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )

        value = if (grouped.isEmpty()) {
            null
        } else {
            MovieWatchInfo(
                grouped.map { (language, sources) ->
                    MovieWatchInfo.Source(
                        language = language,
                        sources = sources
                    )
                }
            )
        }
    }.value
}