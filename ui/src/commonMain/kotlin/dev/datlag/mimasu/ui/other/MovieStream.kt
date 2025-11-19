package dev.datlag.mimasu.ui.other

import dev.datlag.mimasu.extension.model.Movie as Extension

expect class MovieStream {
    val state: MovieState
    val isAvailable: Boolean

    suspend fun getStream(): Extension.Response?
}