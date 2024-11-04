package dev.datlag.mimasu.ui.navigation.screen.movie

import dev.datlag.mimasu.tmdb.api.Details
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface MovieComponent : Component {

    val trending: Trending.Response.Media.Movie

    val movie: Flow<Details.Movie?>

    fun back()
}