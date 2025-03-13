package dev.datlag.mimasu.tmdb.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.datlag.mimasu.core.withNonEmptyContext
import dev.datlag.mimasu.tmdb.api.Search
import dev.datlag.mimasu.tmdb.model.PagedResponse
import dev.datlag.mimasu.tmdb.model.search.Multi
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.call.body
import kotlin.coroutines.CoroutineContext

class SearchRepository(
    @Secret private val apiKey: String,
    private val search: Search,
    private val language: String,
    private val context: CoroutineContext
) {

    inner class MultiPaging(
        private val query: String,
        private val includeAdult: Boolean
    ) : PagingSource<Int, Multi>() {

        override fun getRefreshKey(state: PagingState<Int, Multi>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Multi> {
            val key = params.key ?: 1
            val result = withNonEmptyContext(context) {
                suspendCatching {
                    val response = search.multi(
                        apiKey = apiKey,
                        query = query,
                        includeAdult = includeAdult,
                        language = language,
                        page = key
                    )

                    response.body<PagedResponse<Multi>>()
                }
            }

            val data = result.getOrNull()

            return when {
                data != null -> {
                    LoadResult.Page(
                        data = data.results,
                        prevKey = (data.page - 1).takeIf { it >= 1 },
                        nextKey = if (data.page >= data.totalPages || data.results.isEmpty()) null else data.page + 1
                    )
                }
                else -> LoadResult.Error(
                    result.exceptionOrNull()
                        ?: IllegalStateException("Could not load paging data of multi search")
                )
            }
        }

    }

}