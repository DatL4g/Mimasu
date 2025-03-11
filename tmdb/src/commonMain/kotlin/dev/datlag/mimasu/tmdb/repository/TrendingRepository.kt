package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.datlag.mimasu.tmdb.model.trending.Movie
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching

class TrendingRepository(
    @Secret private val apiKey: String,
) {

    inner class MoviesPaging(

    ) : PagingSource<Int, Movie>() {
        override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
            val key = params.key ?: 1
            val result = suspendCatching {

            }

            return when {
                else -> {
                    LoadResult.Error(
                        result.exceptionOrNull() ?: IllegalStateException("Could not load paging data of trending movies")
                    )
                }
            }
        }

    }
}