package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

interface Details {

    @GET("movie/{id}")
    suspend fun movie(
        @Query("api_key") apiKey: String,
        @Path("id") id: Int,
        @Query("language") language: String,
    ): HttpResponse

}