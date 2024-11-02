package dev.datlag.mimasu.ui.navigation

import dev.datlag.mimasu.tmdb.api.Trending
import kotlinx.serialization.Serializable

@Serializable
sealed class RootConfig {

    @Serializable
    data object Initial : RootConfig()

    @Serializable
    data object Login : RootConfig()

    @Serializable
    data class Movie(
        val trending: Trending.Response.Media.Movie
    ) : RootConfig()

    @Serializable
    data object Video : RootConfig()
}