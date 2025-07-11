package dev.datlag.mimasu.ui.other

import kotlinx.serialization.Serializable

@Serializable
sealed interface EpisodeStreamState {

    @Serializable
    data object Initializing : EpisodeStreamState

    @Serializable
    data object Requesting : EpisodeStreamState

    @Serializable
    data class Available(val state: Boolean) : EpisodeStreamState

    @Serializable
    data object Unavailable : EpisodeStreamState
}