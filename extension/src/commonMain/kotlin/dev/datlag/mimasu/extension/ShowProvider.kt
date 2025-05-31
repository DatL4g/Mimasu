package dev.datlag.mimasu.extension

import dev.datlag.mimasu.extension.model.Show

interface ShowProvider {

    suspend fun requestInfo(request: Show.Request): List<Show.Identifier>
}