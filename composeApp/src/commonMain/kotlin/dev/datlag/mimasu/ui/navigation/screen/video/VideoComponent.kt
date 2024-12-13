package dev.datlag.mimasu.ui.navigation.screen.video

import dev.datlag.mimasu.ui.navigation.Component
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface VideoComponent : Component {
    val shownInDialog: Boolean

    val isPlaying: MutableStateFlow<Boolean>
    val aspectRatio: StateFlow<Float>

    var togglePlayListener: ((Boolean) -> Unit)?

    fun updateAspectRatio(value: Float)
    fun togglePlayPause(showControls: Boolean = true)
}