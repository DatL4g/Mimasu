package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

interface Companies {

    /**
     * Get the company details by ID.
     */
    @GET("company/{id}")
    suspend fun details(
        @Query("api_key") apiKey: String,
        @Path("id") id: Int
    ): HttpResponse

    /**
     * Get the company details by ID.
     */
    @GET("company/{id}/alternative_names")
    suspend fun alternativeNames(
        @Query("api_key") apiKey: String,
        @Path("id") id: Int
    ): HttpResponse

    /**
     * Get the company logos by id.
     */
    @GET("company/{id}/images")
    suspend fun images(
        @Query("api_key") apiKey: String,
        @Path("id") id: Int
    ): HttpResponse
}