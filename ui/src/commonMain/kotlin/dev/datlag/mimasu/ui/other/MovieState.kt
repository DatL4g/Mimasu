package dev.datlag.mimasu.ui.other

import kotlinx.serialization.Serializable

@Serializable
sealed interface MovieState {

    @Serializable
    data object Initializing : MovieState

    @Serializable
    data class Available(val state: Boolean) : MovieState

    @Serializable
    data object Unavailable : MovieState
}