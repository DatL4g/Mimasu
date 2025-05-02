package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tmdb.repository.DetailsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class ShowViewModel(
    val detailsRepository: DetailsRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val show: Flow<ShowState> = id.transformLatest { id ->
        when (id) {
            null -> return@transformLatest emit(ShowState.Error(null))
            else -> {
                emit(ShowState.Loading)

                val result = detailsRepository.show(id)
                val show = result.getOrNull()

                return@transformLatest if (show == null) {
                    emit(ShowState.Error(result.exceptionOrNull()))
                } else {
                    emit(ShowState.Success(show))
                }
            }
        }
    }

    val initialShow = Companion.initialShow

    private val _season = MutableStateFlow<Show.Season?>(null)
    val showSeason = _season.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val season: Flow<SeasonState> = combine(id, show, showSeason) { t1, t2, t3 ->
        val currentShow = t2.getOrNull() ?: show.firstOrNull()?.getOrNull()
        val showId = t1?.takeIf { it > 0 } ?: currentShow?.id?.takeIf { it > 0 }
        val seasonNumber = currentShow?.seasons?.indexOf(t3)?.takeIf { it > 0 }
            ?: currentShow?.seasons?.indexOfFirst { it.id > 0 && it.id == t3?.id }?.takeIf { it > 0 }

        if (showId != null && seasonNumber != null) {
            SeasonRequest(
                showId = showId,
                seasonId = seasonNumber
            )
        } else {
            null
        }
    }.transformLatest { request ->
        when (request) {
            null -> return@transformLatest emit(SeasonState.Empty)
            else -> {
                emit(SeasonState.Loading)

                val result = detailsRepository.showSeason(request.showId, request.seasonId)
                val season = result.getOrNull()

                return@transformLatest if (season == null) {
                    emit(SeasonState.Error(result.exceptionOrNull()))
                } else {
                    emit(SeasonState.Success(season))
                }
            }
        }
    }

    fun select(season: Show.Season) = _season.updateAndGet { season }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
        clear()
        _season.update { null }
    }

    @Serializable
    sealed interface ShowState {

        fun getOrNull(): Show? = when (this) {
            is ShowState.Success -> show
            else -> null
        }

        @Serializable
        data object Loading : ShowState

        @Serializable
        data class Success(
            val show: Show
        ) : ShowState

        @Serializable
        data class Error(
            @Transient val throwable: Throwable? = null
        ) : ShowState
    }

    @Serializable
    sealed interface SeasonState {

        fun getOrNull(): Season? = when (this) {
            is SeasonState.Success -> season
            else -> null
        }

        @Serializable
        data object Empty : SeasonState

        @Serializable
        data object Loading : SeasonState

        @Serializable
        data class Success(
            val season: Season
        ) : SeasonState

        @Serializable
        data class Error(
            @Transient val throwable: Throwable? = null
        ) : SeasonState
    }

    @Serializable
    private data class SeasonRequest(
        val showId: Int,
        val seasonId: Int
    )

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