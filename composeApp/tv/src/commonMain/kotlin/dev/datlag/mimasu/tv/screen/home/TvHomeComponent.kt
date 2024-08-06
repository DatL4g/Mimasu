package dev.datlag.mimasu.tv.screen.home

import app.cash.paging.PagingData
import dev.datlag.mimasu.tmdb.api.Trending
import kotlinx.coroutines.flow.Flow

interface TvHomeComponent {

    val trendingMovies: Flow<PagingData<Trending.Response.Media.Movie>>
    val trendingSeries: Flow<PagingData<Trending.Response.Media.TV>>

}