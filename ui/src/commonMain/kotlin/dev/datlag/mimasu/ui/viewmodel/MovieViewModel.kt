package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie
import dev.datlag.mimasu.tmdb.repository.DetailsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update

class MovieViewModel(
    val detailsRepository: DetailsRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val movie: Flow<Movie?> = id.mapLatest { id ->
        id?.let { detailsRepository.movie(it) }
    }

    val initialMovie = Companion.initialMovie

    fun updateFrom(movie: CommonMovie) = Companion.updateFrom(movie)

    fun clear() {
        viewModelScope.cancel()

        Companion.clear()
    }

    override fun onCleared() {
        super.onCleared()

        clear()
    }

    companion object {
        private val _id = MutableStateFlow<Int?>(null)
        val id = _id.asStateFlow()

        private val _initialMovie = MutableStateFlow<CommonMovie?>(null)
        val initialMovie = _initialMovie.asStateFlow()

        fun updateFrom(movie: CommonMovie) {
            _id.update { movie.id }
            _initialMovie.update { movie }
        }

        fun clear() {
            _id.update { null }
            _initialMovie.update { null }
        }
    }
}