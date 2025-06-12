package dev.datlag.mimasu.ui.viewmodel

import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.datlag.mimasu.firebase.firestore.FirebaseFirestoreWrapper
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

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

    fun selectInfo(info: SourceInfo) = _selectedInfo.update { info }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
        clear()
    }

    data class SourceInfo(
        val sourceTitle: String?,
        val sourceLocale: String?,
        val locale: String?
    )

    companion object {
        private val _sources: MutableStateFlow<Map<SourceInfo, Collection<String>>> = MutableStateFlow(
            emptyMap()
        )
        val sources = _sources.asStateFlow()

        private val _selectedInfo = MutableStateFlow<SourceInfo?>(null)
        private val selectedInfo = _selectedInfo.asStateFlow()

        fun updateSources(list: Map<SourceInfo, Collection<String>>): Boolean {
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