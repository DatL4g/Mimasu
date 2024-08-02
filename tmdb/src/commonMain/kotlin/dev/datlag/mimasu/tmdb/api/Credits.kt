package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

interface Credits {

    /**
     * Get a movie or TV credit details by ID.
     */
    @GET("credit/{id}")
    suspend fun details(
        @Query("api_key") apiKey: String,
        @Path("id") id: String
    ): HttpResponse
}