package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.datlag.mimasu.core.withNonEmptyContext
import dev.datlag.mimasu.tmdb.api.Search
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.PagedResponse
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.Response
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.call.body
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
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
    ): SearchResult {
        if (query.isBlank()) {
            return SearchResult.Empty
        }

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

        return SearchResult.from(result)
    }

    @Serializable
    data class SearchResult(
        val people: ImmutableList<People>,
        val movies: ImmutableList<Movie>,
        val series: ImmutableList<TV>,
        val error: Boolean
    ) {
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

        companion object {
            val Empty = SearchResult(
                people = persistentListOf(),
                movies = persistentListOf(),
                series = persistentListOf(),
                error = false
            )

            internal fun from(result: Result<PagedResponse<Response>>): SearchResult {
                val result = result.getOrNull() ?: return Empty.copy(error = result.isFailure)

                val people = result.results.filterIsInstance<People>()
                val movies = result.results.filterIsInstance<Movie>()
                val series = result.results.filterIsInstance<TV>()

                return if (people.isEmpty() && movies.isEmpty() && series.isEmpty()) {
                    Empty
                } else {
                    SearchResult(
                        people = people.toImmutableList(),
                        movies = movies.toImmutableList(),
                        series = series.toImmutableList(),
                        error = false
                    )
                }
            }
        }
    }

}