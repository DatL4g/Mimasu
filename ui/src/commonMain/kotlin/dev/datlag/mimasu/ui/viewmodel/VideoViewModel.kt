package dev.datlag.mimasu.ui.viewmodel

import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class VideoViewModel : ViewModel() {

    private val sources = Companion.sources
    val allLanguages = sources.map { it.keys }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = sources.value.keys
    )
    val selectedLanguage = Companion.selectedLanguage

    val selectedSource = combine(sources, selectedLanguage) { allSources, language ->
        allSources[language]?.ifEmpty { null } ?: language?.let {
            getBestLanguageMatch(it, allSources.keys)
        }?.let { allSources[it]?.ifEmpty { null } } ?: emptyList()
    }

    fun selectLanguage(lang: String) = _selectedLanguage.update {
        getBestLanguageMatch(lang, sources.value.keys)
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
        clear()
    }

    companion object {
        private val _sources: MutableStateFlow<Map<String, Collection<String>>> = MutableStateFlow(
            emptyMap()
        )
        val sources = _sources.asStateFlow()

        private val _selectedLanguage = MutableStateFlow<String?>(null)
        private val selectedLanguage = _selectedLanguage.asStateFlow()

        fun updateSources(list: Map<String, Collection<String>>) = _sources.updateAndGet {
            list.filterNot { (_, value) ->
                value.isEmpty()
            }
        }.also { updated ->
            _selectedLanguage.update { getBestLanguageMatch(Locale.current.language, updated.keys) }
        }

        fun clear() {
            _sources.update { emptyMap() }
            _selectedLanguage.update { null }
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