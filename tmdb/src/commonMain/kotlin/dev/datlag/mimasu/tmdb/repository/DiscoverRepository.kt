package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.mimasu.core.typeOf
import dev.datlag.mimasu.core.withNonEmptyContext
import dev.datlag.mimasu.kache.async
import dev.datlag.mimasu.tmdb.api.Discover
import dev.datlag.mimasu.tmdb.model.PagedResponse
import dev.datlag.mimasu.tmdb.model.Response
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.safeCast
import io.ktor.client.call.body
import kotlinx.collections.immutable.toImmutableList
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.days

class DiscoverRepository(
    @Secret private val apiKey: String,
    private val discover: Discover,
    private val language: String,
    private val context: CoroutineContext
) {

    private val tvKache = InMemoryKache<CacheKey, PagedResponse<TV>>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.days
    }

    private suspend inline fun <reified T : Response> pagedRequest(
        page: Int,
        genre: Int
    ): Result<PagedResponse<T>?> = when {
        T::class typeOf TV::class -> suspendCatching {
            tvKache.async(CacheKey(
                genre = genre,
                page = page
            )) {
                val response = discover.tv(
                    apiKey = apiKey,
                    sortBy = "popularity.desc",
                    withStatus = listOf(
                        Show.Status.Returning,
                        Show.Status.InProduction,
                        Show.Status.Ended,
                        Show.Status.Pilot
                    ).joinToString(separator = "|") { it.discoverValue.toString() },
                    withGenres = "$genre",
                    language = language,
                    page = page
                )

                response.body<PagedResponse<TV>>()
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

    data class CacheKey(
        val genre: Int,
        val page: Int
    )

    inner class TVPaging(
        private val genre: Int
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
                pagedRequest<TV>(key, genre)
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
                            ?: IllegalStateException("Could not load paging data of discover tv shows")
                    )
                }
            }
        }
    }
}