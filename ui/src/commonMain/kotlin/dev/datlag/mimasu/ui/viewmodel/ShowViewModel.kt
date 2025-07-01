package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.datlag.mimasu.firebase.firestore.FirebaseFirestoreWrapper
import dev.datlag.mimasu.firebase.firestore.ShowData
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
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class ShowViewModel(
    val detailsRepository: DetailsRepository,
    val firestoreWrapper: FirebaseFirestoreWrapper
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
                    emit(ShowState.Success(show.also {
                        val selectedSeason = it.seasons.singleOrNull()
                            ?: firestoreWrapper.getSeason(id)?.let { s ->
                                it.seasons.firstOrNull { e -> e.seasonNumber == s } ?: if (it.seasons.any { e -> e.seasonNumber <= 0 }) {
                                    it.seasons.elementAtOrNull(s) ?: it.seasons.elementAtOrNull(s - 1)
                                } else {
                                    it.seasons.elementAtOrNull(s - 1) ?: it.seasons.elementAtOrNull(s)
                                }
                            }?: it.seasons.filterNot { s -> s.seasonNumber <= 0 }.singleOrNull()

                        selectedSeason?.let(::select)
                    }))
                }
            }
        }
    }

    val initialShow = Companion.initialShow
    val showSeason = Companion.showSeason

    @OptIn(ExperimentalCoroutinesApi::class)
    val season = showSeason.mapNotNull { s ->
        s?.seasonNumber?.takeIf { it >= 0 }
    }.combine(id) { seasonNumber, showId ->
        val showNotNullId = showId?.takeIf { it > 0 }
            ?: show.firstOrNull()?.getOrNull()?.id?.takeIf { it > 0 }
            ?: return@combine null

        SeasonRequest(
            showId = showNotNullId,
            seasonId = seasonNumber
        )
    }.transformLatest { request ->
        when (request) {
            null -> return@transformLatest emit(SeasonState.Empty)
            else -> {
                emit(SeasonState.Loading)

                val savedSeason = firestoreWrapper.getSeason(
                    tmdbId = request.showId,
                    offlineOnly = true
                )?.takeIf { it >= 0 }

                if (savedSeason != request.seasonId) {
                    firestoreWrapper.selectSeason(
                        ShowData(
                            tmdbId = request.showId,
                            season = request.seasonId
                        )
                    )
                }

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val episodesData = showSeason.mapNotNull { s ->
        s?.seasonNumber?.takeIf { it >= 0 }
    }.combine(id) { seasonNumber, showId ->
        val showNotNullId = showId?.takeIf { it > 0 }
            ?: show.firstOrNull()?.getOrNull()?.id?.takeIf { it > 0 }
            ?: return@combine null

        SeasonRequest(
            showId = showNotNullId,
            seasonId = seasonNumber
        )
    }.transformLatest {
        return@transformLatest if (it == null) {
            emit(it)
        } else {
            emitAll(firestoreWrapper.episodesFor(it.showId, it.seasonId).flow)
        }
    }

    suspend fun markAsWatched(tmdbId: Int, seasonNumber: Int, episode: Season.Episode) {
        val data = ShowData.EpisodeData(
            number = episode.episodeNumber,
            markedAsWatched = true
        )

        return firestoreWrapper.updateEpisode(tmdbId, seasonNumber, data)
    }

    suspend fun markAsUnwatched(tmdbId: Int, seasonNumber: Int, episode: Season.Episode)  {
        val data = ShowData.EpisodeData(
            number = episode.episodeNumber,
            markedAsWatched = false
        )

        return firestoreWrapper.updateEpisode(tmdbId, seasonNumber, data)
    }

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

        private val _season = MutableStateFlow<Show.Season?>(null)
        val showSeason = _season.asStateFlow()

        fun updateFrom(show: TV) {
            _id.update { show.id }
            _initialShow.update { show }
            _season.update { null }
        }

        fun clear() {
            _id.update { null }
            _initialShow.update { null }
            _season.update { null }
        }
    }
}