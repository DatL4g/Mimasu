package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.repository.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.updateAndGet

data class SearchViewModel(
    val searchRepository: SearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow<String?>(null)
    val query = _query.asStateFlow()

    private val _includeAdult = MutableStateFlow(false)
    val includeAdult = _includeAdult.asStateFlow()

    private val searchInfo = combine(query, includeAdult) { q, a ->
        SearchInfo(q?.trim()?.takeIf { it.length >= 2 } ?: "", a)
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val people = searchInfo.flatMapLatest { info ->
        if (info.query.isBlank()) {
            return@flatMapLatest flowOf(PagingData.empty())
        }

        Pager(
            config = PagingConfig(pageSize = 1)
        ) {
            searchRepository.PersonPaging(info.query)
        }.flow.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val movies = searchInfo.flatMapLatest { info ->
        if (info.query.isBlank()) {
            return@flatMapLatest flowOf(PagingData.empty())
        }

        Pager(
            config = PagingConfig(pageSize = 1)
        ) {
            searchRepository.MoviePaging(info.query)
        }.flow.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tv = searchInfo.flatMapLatest { info ->
        if (info.query.isBlank()) {
            return@flatMapLatest flowOf(PagingData.empty())
        }

        Pager(
            config = PagingConfig(pageSize = 1)
        ) {
            searchRepository.TVPaging(info.query)
        }.flow.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val showsAndMovies = searchInfo.flatMapLatest { info ->
        if (info.query.isBlank()) {
            return@flatMapLatest flowOf(PagingData.empty())
        }

        Pager(
            config = PagingConfig(pageSize = 1)
        ) {
            searchRepository.MultiPaging(info.query)
        }.flow.map { pagingData ->
            pagingData.filter { item ->
                item is Movie || item is TV
            }
        }.cachedIn(viewModelScope)
    }

    fun updateQuery(query: String) = _query.updateAndGet { query.ifBlank { null } }

    private data class SearchInfo(
        val query: String,
        val includeAdult: Boolean
    )

}