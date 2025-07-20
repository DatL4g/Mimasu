package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.datlag.mimasu.core.typeOf
import dev.datlag.mimasu.core.withNonEmptyContext
import dev.datlag.mimasu.tmdb.api.Search
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.PagedResponse
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.Response
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.safeCast
import io.ktor.client.call.body
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

class SearchRepository(
    @Secret private val apiKey: String,
    private val search: Search,
    private val language: String,
    private val context: CoroutineContext
) {


    private suspend inline fun <reified T : Response> pagedRequest(
        page: Int,
        query: String
    ): Result<PagedResponse<T>?> = when {
        T::class typeOf People::class -> suspendCatching {
            val response = search.person(
                apiKey = apiKey,
                query = query,
                language = language,
                page = page
            )

            response.body<PagedResponse<People>>()
        }
        T::class typeOf Movie::class -> suspendCatching {
            val response = search.movie(
                apiKey = apiKey,
                query = query,
                language = language,
                page = page
            )

            response.body<PagedResponse<Movie>>()
        }
        T::class typeOf TV::class -> suspendCatching {
            val response = search.tv(
                apiKey = apiKey,
                query = query,
                language = language,
                page = page
            )

            response.body<PagedResponse<TV>>()
        }
        T::class typeOf Response::class -> suspendCatching {
            val response = search.multi(
                apiKey = apiKey,
                query = query,
                language = language,
                page = page
            )

            response.body<PagedResponse<Response>>()
        }
        else -> throw IllegalArgumentException("Unsupported type: ${T::class}")
    }.mapCatching { result ->
        result.safeCast() ?: result.results.filterIsInstance<T>().ifEmpty { null }?.let {
            PagedResponse(
                page = result.page,
                results = it.toImmutableList(),
                totalPages = result.totalPages,
                totalResults = result.totalResults
            )
        } ?: result as? PagedResponse<T>
    }

    inner class PersonPaging(
        private val query: String
    ) : PagingSource<Int, People>() {
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
                pagedRequest<People>(key, query)
            }

            val data = result.getOrNull()

            return when {
                data != null -> LoadResult.Page(
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
                else -> {
                    LoadResult.Error(
                        result.exceptionOrNull()
                            ?: IllegalStateException("Could not load paging data of person search.")
                    )
                }
            }
        }
    }

    inner class MoviePaging(
        private val query: String
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
                pagedRequest<Movie>(key, query)
            }

            val data = result.getOrNull()

            return when {
                data != null -> LoadResult.Page(
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
                else -> {
                    LoadResult.Error(
                        result.exceptionOrNull()
                            ?: IllegalStateException("Could not load paging data of movie search.")
                    )
                }
            }
        }
    }

    inner class TVPaging(
        private val query: String
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
                pagedRequest<TV>(key, query)
            }

            val data = result.getOrNull()

            return when {
                data != null -> LoadResult.Page(
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
                else -> {
                    LoadResult.Error(
                        result.exceptionOrNull()
                            ?: IllegalStateException("Could not load paging data of tv search.")
                    )
                }
            }
        }
    }

    inner class MultiPaging(
        private val query: String
    ) : PagingSource<Int, Response>() {
        override fun getRefreshKey(state: PagingState<Int, Response>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Response> {
            val key = params.key ?: 1
            val result = withNonEmptyContext(context) {
                pagedRequest<Response>(key, query)
            }

            val data = result.getOrNull()

            return when {
                data != null -> LoadResult.Page(
                    data = data.results,
                    prevKey = (data.page - 1).takeIf { it >= 1 },
                    nextKey = if (data.page >= data.totalPages || data.results.isEmpty()) {
                        null
                    } else {
                        data.page + 1
                    }
                )
                else -> {
                    LoadResult.Error(
                        result.exceptionOrNull()
                            ?: IllegalStateException("Could not load paging data of multi search.")
                    )
                }
            }
        }
    }

}