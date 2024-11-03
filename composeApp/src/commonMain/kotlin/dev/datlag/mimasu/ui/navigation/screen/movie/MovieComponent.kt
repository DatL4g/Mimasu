package dev.datlag.mimasu.ui.navigation.screen.movie

import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.ui.navigation.Component

interface MovieComponent : Component {

    val trending: Trending.Response.Media.Movie

    fun back()
}