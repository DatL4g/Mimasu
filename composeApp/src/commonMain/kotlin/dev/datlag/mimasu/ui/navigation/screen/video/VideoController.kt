package dev.datlag.mimasu.ui.navigation.screen.video

import kotlinx.coroutines.flow.StateFlow

interface VideoController {

    val aspectRatio: StateFlow<Float>
    val isPlaying: StateFlow<Boolean>
    var controlsAvailable: Boolean

    fun <T> getPlayer(): T?

    fun togglePlayPause(showControls: Boolean = true)

}