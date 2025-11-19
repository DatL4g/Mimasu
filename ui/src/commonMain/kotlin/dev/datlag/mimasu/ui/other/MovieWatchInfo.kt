package dev.datlag.mimasu.ui.other

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.Movie as IMovie

@Composable
expect fun rememberMovieAvailability(movie: Movie?, initial: IMovie?): MovieStream