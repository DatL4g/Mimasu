package dev.datlag.mimasu.extension

import dev.datlag.mimasu.extension.model.Show

interface ShowProvider {

    suspend fun initialize()
    suspend fun requestId(request: Show.Request): Boolean
    suspend fun requestEpisode(tmdbId: Int, request: Show.EpisodeRequest): Boolean
    suspend fun requestStream(tmdbId: Int, request: Show.EpisodeRequest): Show.Response?
}