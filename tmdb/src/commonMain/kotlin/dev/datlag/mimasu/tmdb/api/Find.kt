package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

interface Find {

    /**
     * Find data by external ID's.
     */
    @GET("find/{id}")
    suspend fun find(
        @Query("api_key") apiKey: String,
        @Path("id") id: String,
        @Query("external_source") source: String,
        @Query("language") language: String,
    ): HttpResponse
}