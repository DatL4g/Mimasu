package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.mimasu.core.typeOf
import dev.datlag.mimasu.core.withNonEmptyContext
import dev.datlag.mimasu.kache.async
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tmdb.model.PagedResponse
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.Response
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.trending.TimeWindow
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.safeCast
import io.ktor.client.call.body
import kotlinx.collections.immutable.toImmutableList
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.days

@ConsistentCopyVisibility
data class TrendingRepository internal constructor(
    @Secret private val apiKey: String,
    private val trending: Trending,
    private val language: String,
    private val context: CoroutineContext
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

    private val tvDayKache = InMemoryKache<Int, PagedResponse<TV>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val tvWeekKache = InMemoryKache<Int, PagedResponse<TV>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val peopleDayKache = InMemoryKache<Int, PagedResponse<People>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val peopleWeekKache = InMemoryKache<Int, PagedResponse<People>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private fun movieCache(window: TimeWindow) = when (window) {
        is TimeWindow.Day -> movieDayKache
        is TimeWindow.Week -> movieWeekKache
    }

    private fun tvCache(window: TimeWindow) = when (window) {
        is TimeWindow.Day -> tvDayKache
        is TimeWindow.Week -> tvWeekKache
    }

    private fun peopleCache(window: TimeWindow) = when (window) {
        is TimeWindow.Day -> peopleDayKache
        is TimeWindow.Week -> peopleWeekKache
    }

    private suspend inline fun <reified T : Response> pagedRequest(
        page: Int,
        window: TimeWindow
    ): Result<PagedResponse<T>?> = when {
        T::class typeOf Movie::class -> suspendCatching {
            movieCache(window).async(page) {
                val response = trending.movies(
                    apiKey = apiKey,
                    window = window,
                    language = language,
                    page = page
                )

                response.body<PagedResponse<Movie>>()
            }
        }
        T::class typeOf TV::class -> suspendCatching {
            tvCache(window).async(page) {
                val response = trending.tv(
                    apiKey = apiKey,
                    window = window,
                    language = language,
                    page = page
                )

                response.body<PagedResponse<TV>>()
            }
        }
        T::class typeOf People::class -> suspendCatching {
            peopleCache(window).async(page) {
                val response = trending.people(
                    apiKey = apiKey,
                    window = window,
                    language = language,
                    page = page
                )

                response.body<PagedResponse<People>>()
            }
        }

        else -> throw IllegalArgumentException("Unsupported type: ${T::class}")
    }.mapCatching { result ->
        result.safeCast() ?: result?.results?.filterIsInstance<T>()?.ifEmpty { null }?.let {
            PagedResponse(
                page = result.page,
                results = it.toImmutableList(),
                totalPages = result.totalPages,
                totalResults = result.totalResults
            )
        } ?: result as? PagedResponse<T>
    }

    inner class MoviesPaging(
        private val window: TimeWindow
    ) : PagingSource<Int, Movie>() {
        private val loadedKeys = hashSetOf<Int>()

        override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
            loadedKeys.clear()

            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
            val key = params.key ?: 1
            val result = withNonEmptyContext(context) {
                pagedRequest<Movie>(key, window)
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    LoadResult.Page(
                        data = data.results.distinctBy { it.id }.filterNot {
                            loadedKeys.contains(it.id)
                        }.also {
                            loadedKeys.addAll(it.map { p -> p.id })
                        },
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
                        result.exceptionOrNull()
                            ?: IllegalStateException("Could not load paging data of trending movies")
                    )
                }
            }
        }
    }

    inner class TVPaging(
        private val window: TimeWindow
    ) : PagingSource<Int, TV>() {
        private val loadedKeys = hashSetOf<Int>()

        override fun getRefreshKey(state: PagingState<Int, TV>): Int? {
            loadedKeys.clear()

            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TV> {
            val key = params.key ?: 1
            val result = withNonEmptyContext(context) {
                pagedRequest<TV>(key, window)
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    LoadResult.Page(
                        data = data.results.distinctBy { it.id }.filterNot {
                            loadedKeys.contains(it.id)
                        }.also {
                            loadedKeys.addAll(it.map { p -> p.id })
                        },
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
                        result.exceptionOrNull()
                            ?: IllegalStateException("Could not load paging data of trending tv shows")
                    )
                }
            }
        }
    }

    inner class PeoplePaging(
        private val window: TimeWindow
    ): PagingSource<Int, People>() {
        private val loadedKeys = hashSetOf<Int>()

        override fun getRefreshKey(state: PagingState<Int, People>): Int? {
            loadedKeys.clear()

            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, People> {
            val key = params.key ?: 1
            val result = withNonEmptyContext(context) {
                pagedRequest<People>(key, window)
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    LoadResult.Page(
                        data = data.results.distinctBy { it.id }.filterNot {
                            loadedKeys.contains(it.id)
                        }.also {
                            loadedKeys.addAll(it.map { p -> p.id })
                        },
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
                        result.exceptionOrNull()
                            ?: IllegalStateException("Could not load paging data of trending people")
                    )
                }
            }
        }
    }
}