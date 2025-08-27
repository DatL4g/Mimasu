package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.firebase.firestore.FirebaseFirestoreWrapper
import dev.datlag.mimasu.firebase.firestore.MovieData
import dev.datlag.mimasu.firebase.firestore.ShowData
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tmdb.repository.DetailsRepository
import dev.datlag.tooling.async.VirtualIO
import dev.datlag.tooling.safeSubSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseViewModel(
    val firestoreWrapper: FirebaseFirestoreWrapper,
    private val detailsRepository: DetailsRepository
) : ViewModel() {

    private val bookmarkedMovieData = firestoreWrapper.bookmarkedMovies
    private val bookmarkedShowData = firestoreWrapper.bookmarkedShows

    @OptIn(ExperimentalCoroutinesApi::class)
    val hasBookmarkedMovies = bookmarkedMovieData.mapLatest {
        it.isNotEmpty()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val hasBookmarkedShows = bookmarkedShowData.mapLatest {
        it.isNotEmpty()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookmarkedMovies = bookmarkedMovieData.transformLatest {
        val ids = it.mapNotNull { m -> m.tmdbId.takeIf { id -> id > 0 } }

        return@transformLatest emitAll(
            Pager(
                config = PagingConfig(pageSize = 10, enablePlaceholders = false),
                pagingSourceFactory = {
                    BookmarkedMoviesPagingSource(
                        tmdbIds = ids
                    )
                }
            ).flow
        )
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookmarkedShows = bookmarkedShowData.transformLatest {
        val ids = it.mapNotNull { s -> s.tmdbId.takeIf { id -> id > 0 } }

        return@transformLatest emitAll(
            Pager(
                config = PagingConfig(pageSize = 10, enablePlaceholders = false),
                pagingSourceFactory = {
                    BookmarkedShowsPagingSource(
                        tmdbIds = ids
                    )
                }
            ).flow
        )
    }.cachedIn(viewModelScope)

    fun bookmark(bookmarked: Boolean, movie: Movie) = viewModelScope.launch {
        firestoreWrapper.bookmark(
            MovieData(
                bookmarked = bookmarked,
                tmdbId = movie.id,
                imdbId = movie.imdbId,
            )
        )
    }

    /**
     * Bookmarks or unbookmarks show.
     *
     * @return amount of bookmarked items.
     */
    suspend fun bookmark(bookmarked: Boolean, show: Show): Int = withContext(Dispatchers.VirtualIO) {
        return@withContext firestoreWrapper.bookmark(
            ShowData(
                _bookmarked = bookmarked,
                tmdbId = show.id,
                imdbId = show.imdbId,
                numberOfSeasons = show.numberOfSeasons.takeIf { it > 0 }
            )
        ).size
    }

    suspend fun isMovieBookmarked(tmdbId: Int): Boolean {
        return firestoreWrapper.isMovieBookmarked(tmdbId)
    }

    suspend fun isShowBookmarked(tmdbId: Int): Boolean {
        return firestoreWrapper.isShowBookmarked(tmdbId)
    }

    inner class BookmarkedMoviesPagingSource(
        private val tmdbIds: List<Int>,
    ) : PagingSource<Int, Movie>() {

        override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
            val position = params.key ?: 0
            val pageSize = params.loadSize

            val toIndex = (position + pageSize).coerceAtMost(tmdbIds.size)
            val pageIds = tmdbIds.safeSubSet(position, toIndex)

            val movies = coroutineScope {
                pageIds.map { id -> async {
                    detailsRepository.movie(id).getOrNull()
                } }.awaitAll().filterNotNull()
            }

            return LoadResult.Page(
                data = movies,
                prevKey = if (position == 0) null else position - pageSize,
                nextKey = if (toIndex >= tmdbIds.size) null else toIndex
            )
        }
    }

    inner class BookmarkedShowsPagingSource(
        private val tmdbIds: List<Int>
    ) : PagingSource<Int, Show>() {

        override fun getRefreshKey(state: PagingState<Int, Show>): Int? {
            return state.anchorPosition?.let { anchorPos ->
                val anchorPage = state.closestPageToPosition(anchorPos)

                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Show> {
            val position = params.key ?: 0
            val pageSize = params.loadSize

            val toIndex = (position + pageSize).coerceAtMost(tmdbIds.size)
            val pageIds = tmdbIds.safeSubSet(position, toIndex)

            val shows = coroutineScope {
                pageIds.map { id -> async {
                    detailsRepository.show(id).getOrNull()
                } }.awaitAll().filterNotNull()
            }

            return LoadResult.Page(
                data = shows,
                prevKey = if (position == 0) null else position - pageSize,
                nextKey = if (toIndex >= tmdbIds.size) null else toIndex
            )
        }
    }

}