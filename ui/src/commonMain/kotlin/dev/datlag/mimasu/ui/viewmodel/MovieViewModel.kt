package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.repository.DetailsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

class MovieViewModel(
    val detailsRepository: DetailsRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val movie: Flow<State> = id.transformLatest { id ->
        when (id) {
            null -> return@transformLatest emit(State.Error(null))
            else -> {
                emit(State.Loading)

                val result = detailsRepository.movie(id)
                val movie = result.getOrNull()

                return@transformLatest if (movie == null) {
                    emit(State.Error(result.exceptionOrNull()))
                } else {
                    emit(State.Success(movie))
                }
            }
        }
    }

    val initialMovie = Companion.initialMovie

    fun updateFrom(movie: CommonMovie) = Companion.updateFrom(movie)

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
        clear()
    }

    @Serializable
    sealed interface State {

        fun getOrNull(): Movie? = when (this) {
            is State.Success -> movie
            else -> null
        }

        @Serializable
        data object Loading : State

        @Serializable
        data class Success(
            val movie: Movie
        ) : State

        @Serializable
        data class Error(
            @Transient val throwable: Throwable? = null
        ) : State
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