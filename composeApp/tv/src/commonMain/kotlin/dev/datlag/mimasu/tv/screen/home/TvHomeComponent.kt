package dev.datlag.mimasu.tv.screen.home

import app.cash.paging.PagingData
import dev.datlag.mimasu.tmdb.api.Trending
import kotlinx.coroutines.flow.Flow

interface TvHomeComponent {

    val trendingDayMovies: Flow<PagingData<Trending.Response.Media.Movie>>
    val trendingWeekMovies: Flow<PagingData<Trending.Response.Media.Movie>>
    val trendingDayShows: Flow<PagingData<Trending.Response.Media.TV>>
    val trendingWeekShows: Flow<PagingData<Trending.Response.Media.TV>>

}