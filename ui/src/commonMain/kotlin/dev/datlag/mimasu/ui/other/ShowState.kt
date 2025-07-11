package dev.datlag.mimasu.ui.other

import kotlinx.serialization.Serializable

@Serializable
sealed interface ShowState {

    @Serializable
    data object Initializing : ShowState

    @Serializable
    data class Available(val state: Boolean) : ShowState

    @Serializable
    data object Unavailable : ShowState
}
