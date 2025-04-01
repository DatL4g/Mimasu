package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.datlag.mimasu.tmdb.repository.TvSeriesListsRepository

class TvSeriesListsViewModel(
    val tvSeriesListsRepository: TvSeriesListsRepository
) : ViewModel() {

    val airingToday = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        tvSeriesListsRepository.AiringTodayPaging()
    }.flow.cachedIn(viewModelScope)

    val onTheAir = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        tvSeriesListsRepository.OnTheAirPaging()
    }.flow.cachedIn(viewModelScope)

    val popular = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        tvSeriesListsRepository.PopularPaging()
    }.flow.cachedIn(viewModelScope)

    val topRated = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        tvSeriesListsRepository.TopRatedPaging()
    }.flow.cachedIn(viewModelScope)
}