package dev.datlag.mimasu.tmdb.repository

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tmdb.model.TrendingWindow
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.safeCast
import io.ktor.client.call.body
import kotlin.time.Duration.Companion.days

data class TrendingRepository internal constructor(
    @Secret private val apiKey: String,
    private val trending: Trending,
    private val language: String
) {

    private val movieDayKache = InMemoryKache<Int, Trending.Response>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val movieWeekKache = InMemoryKache<Int, Trending.Response>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val tvDayKache = InMemoryKache<Int, Trending.Response>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val tvWeekKache = InMemoryKache<Int, Trending.Response>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private fun movieCache(window: TrendingWindow) = when (window) {
        is TrendingWindow.Day -> movieDayKache
        is TrendingWindow.Week -> movieWeekKache
    }

    private fun tvCache(window: TrendingWindow) = when (window) {
        is TrendingWindow.Day -> tvDayKache
        is TrendingWindow.Week -> tvWeekKache
    }

    inner class MoviesPaging(
        private val window: TrendingWindow
    ) : PagingSource<Int, Trending.Response.Media.Movie>() {

        override suspend fun load(
            params: PagingSourceLoadParams<Int>
        ): PagingSourceLoadResult<Int, Trending.Response.Media.Movie> {
            val key = params.key ?: 1
            val result = suspendCatching {
                movieCache(window).getOrPut(key) {
                    val response = trending.movies(
                        apiKey = apiKey,
                        window = window.value,
                        language = language,
                        page = key
                    )

                    response.body<Trending.Response>()
                }
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    PagingSourceLoadResultPage(
                        data = data.results.mapNotNull { it.safeCast<Trending.Response.Media.Movie>() },
                        prevKey = (data.page - 1).takeIf { it >= 1 },
                        nextKey = if (data.page >= data.totalPages || data.results.isEmpty()) null else data.page + 1
                    ) as PagingSourceLoadResult<Int, Trending.Response.Media.Movie>
                }
                else -> {
                    PagingSourceLoadResultError<Int, Trending.Response.Media.Movie>(
                        result.exceptionOrNull() ?: IllegalStateException("Could not load paging data of trending movies")
                    ) as PagingSourceLoadResult<Int, Trending.Response.Media.Movie>
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Trending.Response.Media.Movie>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }

    inner class TVPaging(
        private val window: TrendingWindow
    ) : PagingSource<Int, Trending.Response.Media.TV>() {

        override suspend fun load(
            params: PagingSourceLoadParams<Int>
        ): PagingSourceLoadResult<Int, Trending.Response.Media.TV> {
            val key = params.key ?: 1
            val result = suspendCatching {
                tvCache(window).getOrPut(key) {
                    val response = trending.tv(
                        apiKey = apiKey,
                        window = TrendingWindow.Day.value,
                        language = language,
                        page = key
                    )

                    response.body<Trending.Response>()
                }
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    PagingSourceLoadResultPage(
                        data = data.results.mapNotNull { it.safeCast<Trending.Response.Media.TV>() },
                        prevKey = (data.page - 1).takeIf { it >= 1 },
                        nextKey = if (data.page >= data.totalPages || data.results.isEmpty()) null else data.page + 1
                    ) as PagingSourceLoadResult<Int, Trending.Response.Media.TV>
                }
                else -> {
                    PagingSourceLoadResultError<Int, Trending.Response.Media.TV>(
                        result.exceptionOrNull() ?: IllegalStateException("Could not load paging data of trending tv")
                    ) as PagingSourceLoadResult<Int, Trending.Response.Media.TV>
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Trending.Response.Media.TV>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }

    inner class PeoplePaging : PagingSource<Int, Trending.Response.Media.Person>() {

        private val peopleKache = InMemoryKache<Int, Trending.Response>(
            maxSize = 5 * 1024 * 1024
        ) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = 1.days
        }

        override suspend fun load(
            params: PagingSourceLoadParams<Int>
        ): PagingSourceLoadResult<Int, Trending.Response.Media.Person> {
            val key = params.key ?: 1
            val result = suspendCatching {
                peopleKache.getOrPut(key) {
                    val response = trending.people(
                        apiKey = apiKey,
                        window = TrendingWindow.Day.value,
                        language = language,
                        page = key
                    )

                    response.body<Trending.Response>()
                }
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    PagingSourceLoadResultPage(
                        data = data.results.mapNotNull { it.safeCast<Trending.Response.Media.Person>() },
                        prevKey = (data.page - 1).takeIf { it >= 1 },
                        nextKey = if (data.page >= data.totalPages || data.results.isEmpty()) null else data.page + 1
                    ) as PagingSourceLoadResult<Int, Trending.Response.Media.Person>
                }
                else -> {
                    PagingSourceLoadResultError<Int, Trending.Response.Media.Person>(
                        result.exceptionOrNull() ?: IllegalStateException("Could not load paging data of trending people")
                    ) as PagingSourceLoadResult<Int, Trending.Response.Media.Person>
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Trending.Response.Media.Person>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }
}