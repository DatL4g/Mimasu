package dev.datlag.mimasu.tv.screen.home

import dev.datlag.mimasu.tmdb.api.Trending
import kotlinx.coroutines.flow.Flow

interface TvHomeComponent {

    val trendingMovies: Flow<Trending.Response?>
    val trendingSeries: Flow<Trending.Response?>

}