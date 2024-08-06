package dev.datlag.mimasu.ui.navigation.screen.initial.home

import app.cash.paging.PagingData
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tv.screen.home.TvHomeComponent
import dev.datlag.mimasu.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface HomeComponent : TvHomeComponent, Component {
    val trendingPeople: Flow<PagingData<Trending.Response.Media.Person>>
}