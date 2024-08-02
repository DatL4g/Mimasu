package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

interface Certifications {

    /**
     * Get an up to date list of the officially supported movie certifications on TMDB.
     */
    @GET("certification/movie/list")
    suspend fun movie(
        @Query("api_key") apiKey: String
    ): HttpResponse

    /**
     * Get an up to date list of the officially supported TV certifications on TMDB.
     */
    @GET("certification/tv/list")
    suspend fun tv(
        @Query("api_key") apiKey: String
    ): HttpResponse
}