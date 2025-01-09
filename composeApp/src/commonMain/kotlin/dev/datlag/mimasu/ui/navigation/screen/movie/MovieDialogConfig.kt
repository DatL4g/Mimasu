package dev.datlag.mimasu.ui.navigation.screen.movie

import dev.datlag.mimasu.tmdb.api.Details
import kotlinx.serialization.Serializable

@Serializable
sealed class MovieDialogConfig {

    @Serializable
    data class Cast(
        val cast: Details.Movie.Credits.Cast
    ) : MovieDialogConfig()
}