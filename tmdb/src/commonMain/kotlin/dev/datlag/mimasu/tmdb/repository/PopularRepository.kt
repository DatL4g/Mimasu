package dev.datlag.mimasu.tmdb.repository

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.mimasu.tmdb.api.Discover
import dev.datlag.mimasu.tmdb.model.DiscoverSorting
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.call.body
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration.Companion.days

data class PopularRepository internal constructor(
    @Secret private val apiKey: String,
    private val discover: Discover,
    private val language: String,
    private val region: String?
) {

    private val movieKache = InMemoryKache<Int, Discover.MovieResponse>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val tvKache = InMemoryKache<Int, Discover.TVResponse>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    inner class MoviesPaging : PagingSource<Int, Discover.MovieResponse.Movie>() {

        override suspend fun load(
            params: PagingSourceLoadParams<Int>
        ): PagingSourceLoadResult<Int, Discover.MovieResponse.Movie> {
            val key = params.key ?: 1
            val result = suspendCatching {
                movieKache.getOrPut(key) {
                    val response = discover.movie(
                        apiKey = apiKey,
                        language = language,
                        region = region,
                        includeVideo = false,
                        includeAdult = false,
                        sortBy = DiscoverSorting.Movie.Popularity(DiscoverSorting.Direction.Descending).toString(),
                        page = key
                    )

                    response.body<Discover.MovieResponse>()
                }
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    PagingSourceLoadResultPage(
                        data = data.results.toImmutableList(),
                        prevKey = (data.page - 1).takeIf { it >= 1 },
                        nextKey = if (data.page >= data.totalPages || data.results.isEmpty()) null else data.page + 1
                    ) as PagingSourceLoadResult<Int, Discover.MovieResponse.Movie>
                }
                else -> {
                    PagingSourceLoadResultError<Int, Discover.MovieResponse.Movie>(
                        result.exceptionOrNull() ?: IllegalStateException("Could not load paging data of discover")
                    ) as PagingSourceLoadResult<Int, Discover.MovieResponse.Movie>
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Discover.MovieResponse.Movie>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }

    inner class TVPaging : PagingSource<Int, Discover.TVResponse.TV>() {

        override suspend fun load(
            params: PagingSourceLoadParams<Int>
        ): PagingSourceLoadResult<Int, Discover.TVResponse.TV> {
            val key = params.key ?: 1
            val result = suspendCatching {
                tvKache.getOrPut(key) {
                    val response = discover.tv(
                        apiKey = apiKey,
                        language = language,
                        includeAdult = false,
                        sortBy = DiscoverSorting.Tv.Popularity(DiscoverSorting.Direction.Descending).toString(),
                        page = key
                    )

                    response.body<Discover.TVResponse>()
                }
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    PagingSourceLoadResultPage(
                        data = data.results.toImmutableList(),
                        prevKey = (data.page - 1).takeIf { it >= 1 },
                        nextKey = if (data.page >= data.totalPages || data.results.isEmpty()) null else data.page + 1
                    ) as PagingSourceLoadResult<Int, Discover.TVResponse.TV>
                }
                else -> {
                    PagingSourceLoadResultError<Int, Discover.TVResponse.TV>(
                        result.exceptionOrNull() ?: IllegalStateException("Could not load paging data of discover")
                    ) as PagingSourceLoadResult<Int, Discover.TVResponse.TV>
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Discover.TVResponse.TV>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }
}