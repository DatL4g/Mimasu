package dev.datlag.mimasu.ui.navigation.screen.initial.home

import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tv.screen.home.TvHomeComponent
import dev.datlag.mimasu.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface HomeComponent : TvHomeComponent, Component {
    val trendingPeople: Flow<Trending.Response?>
}