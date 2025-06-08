package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VideoViewModel : ViewModel() {

    val sources = Companion.sources

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
        clear()
    }

    companion object {
        private val _sources: MutableStateFlow<Collection<String>> = MutableStateFlow(emptyList())
        val sources = _sources.asStateFlow()

        fun updateSources(list: Collection<String>) = _sources.update {
            list
        }

        fun clear() {
            _sources.update { emptyList() }
        }
    }
}