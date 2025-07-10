package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import co.touchlab.kermit.Logger
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

    suspend fun querySearch(
        query: String,
        includeAdult: Boolean
    ): Flow<SearchResult> = flow {
        if (query.isBlank()) {
            return@flow emit(SearchResult.Empty)
        }

        emit(SearchResult.Loading)
        val result = withNonEmptyContext(context) {
            suspendCatching {
                val response = search.multi(
                    apiKey = apiKey,
                    query = query,
                    includeAdult = includeAdult,
                    language = language,
                    page = 1
                )

                response.body<PagedResponse<Response>>()
            }
        }

        return@flow emit(SearchResult.from(result))
    }

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
        else -> throw IllegalArgumentException("Unsupported type: ${T::class}")
    }.mapCatching { result ->
        result.safeCast() ?: result.results.filterIsInstance<T>().ifEmpty { null }?.let {
            PagedResponse(
                page = result.page,
                results = it,
                totalPages = result.totalPages,
                totalResults = result.totalResults
            )
        } ?: result as? PagedResponse<T>
    }

    inner class PersonPaging(
        private val query: String
    ) : PagingSource<Int, People>() {
        override fun getRefreshKey(state: PagingState<Int, People>): Int? {
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
                            ?: IllegalStateException("Could not load paging data of person search.")
                    )
                }
            }
        }
    }

    inner class MoviePaging(
        private val query: String
    ) : PagingSource<Int, Movie>() {
        override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
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
                            ?: IllegalStateException("Could not load paging data of movie search.")
                    )
                }
            }
        }
    }

    inner class TVPaging(
        private val query: String
    ) : PagingSource<Int, TV>() {
        override fun getRefreshKey(state: PagingState<Int, TV>): Int? {
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
                            ?: IllegalStateException("Could not load paging data of tv search.")
                    )
                }
            }
        }
    }

    @Serializable
    sealed interface SearchResult {

        @Serializable
        data object Loading : SearchResult

        @Serializable
        data object Error : SearchResult

        @Serializable
        data class Success(
            val people: ImmutableList<People>,
            val movies: ImmutableList<Movie>,
            val series: ImmutableList<TV>
        ) : SearchResult {
            fun hasPeople(): Boolean {
                return people.isNotEmpty()
            }

            fun hasMovies(): Boolean {
                return movies.isNotEmpty()
            }

            fun hasSeries(): Boolean {
                return series.isNotEmpty()
            }

            fun isEmpty(): Boolean {
                return this == Empty || (!hasPeople() && !hasMovies() && !hasSeries())
            }
        }

        companion object {
            val Empty = Success(
                people = persistentListOf(),
                movies = persistentListOf(),
                series = persistentListOf()
            )

            internal fun from(result: Result<PagedResponse<Response>>): SearchResult {
                if (result.isFailure) {
                    Logger.e("Search Result Failure", result.exceptionOrNull())
                    return Error
                }
                val saveResult = result.getOrNull() ?: return Empty

                val people = saveResult.results.filterIsInstance<People>()
                val movies = saveResult.results.filterIsInstance<Movie>()
                val series = saveResult.results.filterIsInstance<TV>()

                return if (people.isEmpty() && movies.isEmpty() && series.isEmpty()) {
                    Empty
                } else {
                    Success(
                        people = people.toImmutableList(),
                        movies = movies.toImmutableList(),
                        series = series.toImmutableList()
                    )
                }
            }
        }
    }

}