package dev.datlag.mimasu.other

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

data object ContentDetails {
    private val _padding = MutableStateFlow(PaddingValues(0.dp))
    val padding = _padding.asStateFlow()

    private val _showingPlayer = MutableStateFlow(false)
    val showingPlayer = _showingPlayer.asStateFlow()

    fun setPadding(value: PaddingValues) = _padding.updateAndGet { value }
    fun setShowingPlayer(value: Boolean) = _showingPlayer.updateAndGet { value }
}