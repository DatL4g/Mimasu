package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.details.Person
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

class PersonViewModel(
    val detailsRepository: DetailsRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val person: Flow<State> = id.transformLatest { id ->
        when (id) {
            null -> return@transformLatest emit(State.Error(null))
            else -> {
                emit(State.Loading)

                val result = detailsRepository.person(id)
                val person = result.getOrNull()

                return@transformLatest if (person == null) {
                    emit(State.Error(result.exceptionOrNull()))
                } else {
                    emit(State.Success(person))
                }
            }
        }
    }

    val initialPeople = Companion.initialPeople
    val initialCast = Companion.initialCast
    val initialCrew = Companion.initialCrew

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
        clear()
    }

    @Serializable
    sealed interface State {

        fun getOrNull(): Person? = when (this) {
            is State.Success -> person
            else -> null
        }

        @Serializable
        data object Loading : State

        @Serializable
        data class Success(
            val person: Person
        ) : State

        @Serializable
        data class Error(
            @Transient val throwable: Throwable? = null
        ) : State
    }

    companion object {
        private val _id = MutableStateFlow<Int?>(null)
        val id = _id.asStateFlow()

        private val _initialPeople = MutableStateFlow<People?>(null)
        val initialPeople = _initialPeople.asStateFlow()

        private val _initialCast = MutableStateFlow<Movie.Credits.Cast?>(null)
        val initialCast = _initialCast.asStateFlow()

        private val _initialCrew = MutableStateFlow<Movie.Credits.Crew?>(null)
        val initialCrew = _initialCrew.asStateFlow()

        fun updateFrom(people: People) {
            _id.update { people.id }
            _initialPeople.update { people }
            _initialCast.update { null }
            _initialCrew.update { null }
        }

        fun updateFrom(cast: Movie.Credits.Cast) {
            _id.update { cast.id }
            _initialPeople.update { null }
            _initialCast.update { cast }
            _initialCrew.update { null }
        }

        fun updateFrom(crew: Movie.Credits.Crew) {
            _id.update { crew.id }
            _initialPeople.update { null }
            _initialCast.update { null }
            _initialCrew.update { crew }
        }

        fun clear() {
            _id.update { null }
            _initialPeople.update { null }
            _initialCast.update { null }
            _initialCrew.update { null }
        }
    }
}