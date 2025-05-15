package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.tmdb.model.details.Movie
import kotlinx.serialization.Serializable
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@Composable
expect fun rememberMovieWatchInfo(movie: Movie?, initial: CommonMovie?): MovieWatchInfo?

@Serializable
data class MovieWatchInfo(
    val mappedSources: List<Source>
) {

    @Serializable
    data class Source(
        val language: String,
        val sources: List<String>
    )
}