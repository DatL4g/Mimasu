package dev.datlag.mimasu.ui.other

import dev.datlag.mimasu.extension.MovieProvider
import dev.datlag.mimasu.extension.model.Movie

actual class MovieStream(
    actual val state: MovieState,
    private val provider: MovieProvider?,
    private val tmdbId: Int?
) {
    actual val isAvailable: Boolean = state is MovieState.Available && state.state

    actual suspend fun getStream(): Movie.Response? {
        if (isAvailable) {
            if (provider != null && tmdbId?.takeIf { it > 0 } != null) {
                return provider.requestStream(tmdbId)
            }
        }
        return null
    }
}