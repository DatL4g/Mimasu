package dev.datlag.mimasu.tmdb.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface DetailState<out T> {

    @Serializable
    data object Loading : DetailState<Nothing>

    @Serializable
    data class Success<T>(
        @Contextual val data: T & Any
    ) : DetailState<T>

    @Serializable
    data class Error<T>(
        @Transient val exception: Throwable? = null
    ) : DetailState<T>
}