package dev.datlag.mimasu.extension

import dev.datlag.mimasu.extension.model.Movie

interface MovieProvider {

    suspend fun initialize()
    suspend fun requestId(request: Movie.Request): Boolean
    suspend fun requestStream(tmdbId: Int): Movie.Response?
}