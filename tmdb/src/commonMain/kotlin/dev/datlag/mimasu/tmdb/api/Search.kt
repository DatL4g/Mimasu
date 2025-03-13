package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.RequestType
import io.ktor.client.statement.HttpResponse

interface Search {

    @GET("search/multi")
    suspend fun multi(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("include_adult") @RequestType(String::class) includeAdult: Boolean,
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse
}