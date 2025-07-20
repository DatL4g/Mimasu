package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.mimasu.core.withNonEmptyContext
import dev.datlag.mimasu.kache.async
import dev.datlag.mimasu.tmdb.api.MovieLists
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.PagedResponse
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.call.body
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.days

class MovieListsRepository(
    @Secret private val apiKey: String,
    private val api: MovieLists,
    private val region: String?,
    private val language: String,
    private val context: CoroutineContext
) {

    private val nowPlayingKache = InMemoryKache<Int, PagedResponse<Movie>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val popularKache = InMemoryKache<Int, PagedResponse<Movie>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val topRatedKache = InMemoryKache<Int, PagedResponse<Movie>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val upcomingKache = InMemoryKache<Int, PagedResponse<Movie>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private fun cache(type: Type) = when (type) {
        is Type.NowPlaying -> nowPlayingKache
        is Type.Popular -> popularKache
        is Type.TopRated -> topRatedKache
        is Type.Upcoming -> upcomingKache
    }

    private suspend fun pagedRequest(
        page: Int,
        type: Type
    ): Result<PagedResponse<Movie>?> = suspendCatching {
        cache(type).async(page) {
            val response = when (type) {
                is Type.NowPlaying -> api.nowPlaying(
                    apiKey = apiKey,
                    language = language,
                    page = page,
                    region = region
                )
                is Type.Popular -> api.popular(
                    apiKey = apiKey,
                    language = language,
                    page = page,
                    region = region
                )
                is Type.TopRated -> api.topRated(
                    apiKey = apiKey,
                    language = language,
                    page = page,
                    region = region
                )
                is Type.Upcoming -> api.upcoming(
                    apiKey = apiKey,
                    language = language,
                    page = page,
                    region = region
                )
            }

            response.body<PagedResponse<Movie>>()
        }
    }

    sealed interface Type {
        data object NowPlaying : Type
        data object Popular : Type
        data object TopRated : Type
        data object Upcoming : Type
    }

    inner class NowPlayingPaging : PagingSource<Int, Movie>() {
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
                pagedRequest(key, Type.NowPlaying)
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
                            ?: IllegalStateException("Could not load paging data of now playing movies")
                    )
                }
            }
        }

    }

    inner class PopularPaging : PagingSource<Int, Movie>() {
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
                pagedRequest(key, Type.Popular)
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
                            ?: IllegalStateException("Could not load paging data of popular movies")
                    )
                }
            }
        }
    }

    inner class TopRatedPaging : PagingSource<Int, Movie>() {
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
                pagedRequest(key, Type.TopRated)
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
                            ?: IllegalStateException("Could not load paging data of top rated movies")
                    )
                }
            }
        }
    }

    inner class UpcomingPaging : PagingSource<Int, Movie>() {
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
                pagedRequest(key, Type.Upcoming)
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
                            ?: IllegalStateException("Could not load paging data of upcoming movies")
                    )
                }
            }
        }
    }
}