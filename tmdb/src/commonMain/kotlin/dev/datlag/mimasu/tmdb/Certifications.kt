package dev.datlag.mimasu.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import io.ktor.client.statement.HttpResponse

interface Certifications {

    @GET("certification/movie/list")
    suspend fun movie(
        @Header("Authorization") authorization: String
    ): HttpResponse

    @GET("certification/tv/list")
    suspend fun tv(
        @Header("Authorization") authorization: String
    ): HttpResponse
}