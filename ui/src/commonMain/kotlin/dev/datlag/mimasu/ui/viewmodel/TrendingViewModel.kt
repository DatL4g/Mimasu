package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.datlag.mimasu.tmdb.model.trending.TimeWindow
import dev.datlag.mimasu.tmdb.repository.TrendingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.updateAndGet

data class TrendingViewModel(
    val trendingRepository: TrendingRepository
) : ViewModel() {

    val timeWindow
        get() = Companion.timeWindow

    val dayMovies = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        trendingRepository.MoviesPaging(TimeWindow.Day)
    }.flow.cachedIn(viewModelScope)

    val weekMovies = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        trendingRepository.MoviesPaging(TimeWindow.Week)
    }.flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val movies = timeWindow.flatMapLatest { window ->
        when (window) {
            is TimeWindow.Day -> dayMovies
            is TimeWindow.Week -> weekMovies
        }
    }.cachedIn(viewModelScope)

    val dayTV = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        trendingRepository.TVPaging(TimeWindow.Day)
    }.flow.cachedIn(viewModelScope)

    val weekTV = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        trendingRepository.TVPaging(TimeWindow.Week)
    }.flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val tv = timeWindow.flatMapLatest { window ->
        when (window) {
            is TimeWindow.Day -> dayTV
            is TimeWindow.Week -> weekTV
        }
    }.cachedIn(viewModelScope)

    val dayPeople = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        trendingRepository.PeoplePaging(TimeWindow.Day)
    }.flow.cachedIn(viewModelScope)

    val weekPeople = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        trendingRepository.PeoplePaging(TimeWindow.Week)
    }.flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val people = timeWindow.flatMapLatest { window ->
        when (window) {
            is TimeWindow.Day -> dayPeople
            is TimeWindow.Week -> weekPeople
        }
    }.cachedIn(viewModelScope)

    fun updateTimeWindow(timeWindow: TimeWindow) = _timeWindow.updateAndGet { timeWindow }
    fun updateToDayTimeWindow() = updateTimeWindow(TimeWindow.Day)
    fun updateToWeekTimeWindow() = updateTimeWindow(TimeWindow.Week)

    companion object {
        private val _timeWindow = MutableStateFlow<TimeWindow>(TimeWindow.Day)
        val timeWindow = _timeWindow.asStateFlow()
    }
}