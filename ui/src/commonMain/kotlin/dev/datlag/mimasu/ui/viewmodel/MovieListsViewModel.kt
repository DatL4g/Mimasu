package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.datlag.mimasu.tmdb.repository.MovieListsRepository

class MovieListsViewModel(
    val movieListsRepository: MovieListsRepository
) : ViewModel() {

    val nowPlaying = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        movieListsRepository.NowPlayingPaging()
    }.flow.cachedIn(viewModelScope)

    val popular = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        movieListsRepository.PopularPaging()
    }.flow.cachedIn(viewModelScope)

    val topRated = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        movieListsRepository.TopRatedPaging()
    }.flow.cachedIn(viewModelScope)

    val upcoming = Pager(
        config = PagingConfig(pageSize = 1)
    ) {
        movieListsRepository.UpcomingPaging()
    }.flow.cachedIn(viewModelScope)

}