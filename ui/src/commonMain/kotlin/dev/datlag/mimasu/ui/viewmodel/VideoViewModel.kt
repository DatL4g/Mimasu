package dev.datlag.mimasu.ui.viewmodel

import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.firebase.firestore.FirebaseFirestoreWrapper
import dev.datlag.mimasu.firebase.firestore.ShowData
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.tooling.async.VirtualIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class VideoViewModel(
    val firestoreWrapper: FirebaseFirestoreWrapper,
) : ViewModel() {

    private val sources = Companion.sources
    val allInfo = sources.map { it.keys }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = sources.value.keys
    )
    val selectedInfo = Companion.selectedInfo

    val selectedSource = combine(sources, selectedInfo) { allSources, info ->
        allSources[info] ?: info?.locale?.let {
            getBestLanguageMatch(it, allSources.keys.mapNotNull { k -> k.locale })
        }?.let {
            allSources.firstNotNullOfOrNull { (k, v) ->
                if (k.locale.equals(it, ignoreCase = true)) {
                    v.ifEmpty { null }
                } else {
                    null
                }
            }
        } ?: emptyList()
    }

    val watchType = Companion.watchType

    fun selectInfo(info: SourceInfo) = _selectedInfo.update { info }

    fun finish(watchType: WatchType) = viewModelScope.launch(Dispatchers.VirtualIO) {
        when (watchType) {
            is WatchType.Show -> {
                firestoreWrapper.updateEpisode(
                    tmdbId = watchType.showInfo.id,
                    seasonNumber = watchType.seasonInfo.seasonNumber,
                    data = ShowData.EpisodeData(
                        number = watchType.episodeInfo.episodeNumber,
                        finished = true
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
        clear()
    }

    @Serializable
    data class SourceInfo(
        val sourceTitle: String?,
        val sourceLocale: String?,
        val locale: String?
    )

    @Serializable
    sealed interface WatchType {

        @Transient
        val title: String

        @Transient
        val subTitle: String?

        @Transient
        val genre: String?

        @Transient
        val albumTitle: String?

        val sources: Map<SourceInfo, Collection<String>>

        @Serializable
        data class Show(
            val showInfo: dev.datlag.mimasu.tmdb.model.details.Show,
            val seasonInfo: Season,
            val episodeInfo: Season.Episode,
            override val sources: Map<SourceInfo, Collection<String>>
        ) : WatchType {

            @Transient
            override val title: String = episodeInfo.name ?: ""

            @Transient
            override val subTitle: String? = seasonInfo.name

            @Transient
            override val genre: String? = showInfo.genres.firstOrNull()?.name

            @Transient
            override val albumTitle: String = showInfo.name
        }
    }

    companion object {
        private val _sources: MutableStateFlow<Map<SourceInfo, Collection<String>>> = MutableStateFlow(
            emptyMap()
        )
        val sources = _sources.asStateFlow()

        private val _selectedInfo = MutableStateFlow<SourceInfo?>(null)
        private val selectedInfo = _selectedInfo.asStateFlow()

        private val _watchType = MutableStateFlow<WatchType?>(null)
        val watchType = _watchType.asStateFlow()

        private fun updateSources(list: Map<SourceInfo, Collection<String>>): Boolean {
            return _sources.updateAndGet {
                list.filterNot { (_, value) ->
                    value.isEmpty()
                }
            }.let { updated ->
                val bestLocale = getBestLanguageMatch(
                    Locale.current.language,
                    updated.keys.mapNotNull { it.locale?.ifBlank { null } }
                )
                val selected = _selectedInfo.updateAndGet {
                    updated.firstNotNullOfOrNull { (k, v) ->
                        if (k.locale.equals(bestLocale, ignoreCase = true)) {
                            k
                        } else {
                            null
                        }
                    }
                }

                selected != null
            }
        }

        fun watch(data: WatchType): Boolean {
            _watchType.update { data }
            return updateSources(data.sources)
        }

        fun clear() {
            _sources.update { emptyMap() }
            _selectedInfo.update { null }
        }

        private fun getBestLanguageMatch(
            requested: String,
            list: Collection<String>
        ): String? {
            return list.firstOrNull {
                it.equals(requested, ignoreCase = true)
            } ?: list.firstOrNull {
                it.startsWith(requested, ignoreCase = true)
            } ?: list.firstOrNull()
        }
    }
}