package dev.datlag.mimasu.extension

import dev.datlag.mimasu.extension.model.Movie

interface MovieProvider {

    suspend fun requestInfo(request: Movie.Request): List<Movie.Response>

}