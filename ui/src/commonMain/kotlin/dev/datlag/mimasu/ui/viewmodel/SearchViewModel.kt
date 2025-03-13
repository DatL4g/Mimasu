package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.datlag.mimasu.tmdb.repository.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
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
    val multiSearch = searchInfo.flatMapLatest { info ->
        Pager(
            config = PagingConfig(pageSize = 1)
        ) {
            searchRepository.MultiPaging(
                query = info.query,
                includeAdult = info.includeAdult
            )
        }.flow
    }.cachedIn(viewModelScope)

    fun updateQuery(query: String) = _query.updateAndGet { query.ifBlank { null } }
    fun updateIncludeAdult(value: Boolean) = _includeAdult.updateAndGet { value }

    private data class SearchInfo(
        val query: String,
        val includeAdult: Boolean
    )

}