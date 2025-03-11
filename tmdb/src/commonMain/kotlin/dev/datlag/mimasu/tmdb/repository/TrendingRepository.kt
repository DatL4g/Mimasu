package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tmdb.model.PagedResponse
import dev.datlag.mimasu.tmdb.model.trending.Movie
import dev.datlag.mimasu.tmdb.model.trending.TimeWindow
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.call.body
import kotlin.time.Duration.Companion.days

@ConsistentCopyVisibility
data class TrendingRepository internal constructor(
    @Secret private val apiKey: String,
    private val trending: Trending,
    private val language: String
) {

    private val movieDayKache = InMemoryKache<Int, PagedResponse<Movie>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val movieWeekKache = InMemoryKache<Int, PagedResponse<Movie>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private fun movieCache(window: TimeWindow) = when (window) {
        is TimeWindow.Day -> movieDayKache
        is TimeWindow.Week -> movieWeekKache
    }

    inner class MoviesPaging(
        private val window: TimeWindow
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
                movieCache(window).getOrPut(key) {
                    val response = trending.movies(
                        apiKey = apiKey,
                        window = window,
                        language = language,
                        page = key
                    )

                    response.body<PagedResponse<Movie>>()
                }
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    LoadResult.Page(
                        data = data.results,
                        prevKey = (data.page - 1).takeIf { it >= 1 },
                        nextKey = if (data.page >= data.totalPages || data.results.isEmpty()) {
                            null
                        } else {
                            data.page + 1
                        }
                    )
                }
                else -> {
                    LoadResult.Error(
                        result.exceptionOrNull() ?: IllegalStateException("Could not load paging data of trending movies")
                    )
                }
            }
        }

    }
}