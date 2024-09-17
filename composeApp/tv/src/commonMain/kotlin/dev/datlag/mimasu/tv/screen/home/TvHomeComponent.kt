package dev.datlag.mimasu.tv.screen.home

import app.cash.paging.PagingData
import dev.datlag.mimasu.tmdb.api.Discover
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tv.PackageAware
import kotlinx.coroutines.flow.Flow

interface TvHomeComponent : PackageAware {

    val trendingDayMovies: Flow<PagingData<Trending.Response.Media.Movie>>
    val trendingWeekMovies: Flow<PagingData<Trending.Response.Media.Movie>>
    val trendingDayShows: Flow<PagingData<Trending.Response.Media.TV>>
    val trendingWeekShows: Flow<PagingData<Trending.Response.Media.TV>>

    val popularMovies: Flow<PagingData<Discover.MovieResponse.Movie>>
    val popularShows: Flow<PagingData<Discover.TVResponse.TV>>

    fun watchVideo()

}