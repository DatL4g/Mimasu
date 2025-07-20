package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.mimasu.core.withNonEmptyContext
import dev.datlag.mimasu.kache.async
import dev.datlag.mimasu.tmdb.api.TvSeriesLists
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.PagedResponse
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.call.body
import kotlinx.datetime.TimeZone
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.days

class TvSeriesListsRepository(
    @Secret private val apiKey: String,
    private val api: TvSeriesLists,
    private val language: String,
    private val context: CoroutineContext
) {
    val timeZone = TimeZone.currentSystemDefault().id

    private val airingTodayKache = InMemoryKache<Int, PagedResponse<TV>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val onTheAirKache = InMemoryKache<Int, PagedResponse<TV>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val popularKache = InMemoryKache<Int, PagedResponse<TV>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private val topRatedKache = InMemoryKache<Int, PagedResponse<TV>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private fun cache(type: Type) = when (type) {
        is Type.AiringToday -> airingTodayKache
        is Type.OnTheAir -> onTheAirKache
        is Type.Popular -> popularKache
        is Type.TopRated -> topRatedKache
    }

    private suspend fun pagedRequest(
        page: Int,
        type: Type
    ): Result<PagedResponse<TV>?> = suspendCatching {
        cache(type).async(page) {
            val response = when (type) {
                is Type.AiringToday -> api.airingToday(
                    apiKey = apiKey,
                    language = language,
                    page = page,
                    timezone = timeZone
                )
                is Type.OnTheAir -> api.onTheAir(
                    apiKey = apiKey,
                    language = language,
                    page = page,
                    timezone = timeZone
                )
                is Type.Popular -> api.popular(
                    apiKey = apiKey,
                    language = language,
                    page = page,
                )
                is Type.TopRated -> api.topRated(
                    apiKey = apiKey,
                    language = language,
                    page = page,
                )
            }

            response.body<PagedResponse<TV>>()
        }
    }

    sealed interface Type {
        data object AiringToday : Type
        data object OnTheAir : Type
        data object Popular : Type
        data object TopRated : Type
    }

    inner class AiringTodayPaging : PagingSource<Int, TV>() {
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
                pagedRequest(key, Type.AiringToday)
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
                            ?: IllegalStateException("Could not load paging data of airing today tv series")
                    )
                }
            }
        }
    }

    inner class OnTheAirPaging : PagingSource<Int, TV>() {
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
                pagedRequest(key, Type.OnTheAir)
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
                            ?: IllegalStateException("Could not load paging data of on the air tv series")
                    )
                }
            }
        }
    }

    inner class PopularPaging : PagingSource<Int, TV>() {
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
                            ?: IllegalStateException("Could not load paging data of popular tv series")
                    )
                }
            }
        }
    }

    inner class TopRatedPaging : PagingSource<Int, TV>() {
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
                            ?: IllegalStateException("Could not load paging data of top rated tv series")
                    )
                }
            }
        }
    }


}