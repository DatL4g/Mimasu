package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

interface Discover {

    @GET("discover/tv")
    suspend fun tv(
        @Query("api_key") apiKey: String,
        @Query("sort_by") sortBy: String,
        @Query("with_status") withStatus: String,
        @Query("with_genres") withGenres: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse
}