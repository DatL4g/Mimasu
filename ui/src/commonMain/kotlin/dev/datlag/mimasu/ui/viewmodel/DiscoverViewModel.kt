package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.datlag.mimasu.tmdb.repository.DiscoverRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

data class DiscoverViewModel(
    val discoverRepository: DiscoverRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val discoverTV = tvGenre.flatMapLatest { genre ->
        if (genre == null) {
            return@flatMapLatest flowOf(PagingData.empty())
        }

        Pager(
            config = PagingConfig(pageSize = 1)
        ) {
            discoverRepository.TVPaging(genre)
        }.flow.cachedIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
        clear()
    }

    companion object {
        private val _tvGenre = MutableStateFlow<Int?>(null)
        private val tvGenre = _tvGenre.asStateFlow()

        private val _movieGenre = MutableStateFlow<Int?>(null)
        val movieGenre = _movieGenre.asStateFlow()

        fun updateTVGenre(id: Int) {
            _movieGenre.update { null }
            _tvGenre.update { id }
        }

        fun clear() {
            _tvGenre.update { null }
            _movieGenre.update { null }
        }
    }
}