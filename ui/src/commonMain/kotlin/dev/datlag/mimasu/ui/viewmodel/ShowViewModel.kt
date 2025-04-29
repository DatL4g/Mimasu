package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tmdb.repository.DetailsRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class ShowViewModel(
    val detailsRepository: DetailsRepository
) : ViewModel() {

    val show: Flow<State> = id.transformLatest { id ->
        when (id) {
            null -> return@transformLatest emit(State.Error(null))
            else -> {
                emit(State.Loading)

                val result = detailsRepository.show(id)
                val show = result.getOrNull()

                return@transformLatest if (show == null) {
                    emit(State.Error(result.exceptionOrNull()))
                } else {
                    emit(State.Success(show))
                }
            }
        }
    }

    val initialShow = Companion.initialShow

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
        clear()
    }

    @Serializable
    sealed interface State {

        fun getOrNull(): Show? = when (this) {
            is State.Success -> show
            else -> null
        }

        @Serializable
        data object Loading : State

        @Serializable
        data class Success(
            val show: Show
        ) : State

        @Serializable
        data class Error(
            @Transient val throwable: Throwable? = null
        ) : State
    }

    companion object {
        private val _id = MutableStateFlow<Int?>(null)
        val id = _id.asStateFlow()

        private val _initialShow = MutableStateFlow<TV?>(null)
        val initialShow = _initialShow.asStateFlow()

        fun updateFrom(show: TV) {
            _id.update { show.id }
            _initialShow.update { show }
        }

        fun clear() {
            _id.update { null }
            _initialShow.update { null }
        }
    }
}