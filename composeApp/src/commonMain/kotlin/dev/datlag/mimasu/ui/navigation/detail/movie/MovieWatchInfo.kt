package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.extension.model.Movie as Extension
import kotlinx.serialization.Serializable
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@Composable
expect fun rememberMovieWatchInfo(movie: Movie?, initial: CommonMovie?): Extension.Response?
