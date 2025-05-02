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
        @Query("append_to_response") appendToResponse: String?
    ): HttpResponse

    @GET("person/{id}")
    suspend fun person(
        @Query("api_key") apiKey: String,
        @Path("id") id: Int,
        @Query("language") language: String,
    ): HttpResponse

    @GET("tv/{id}")
    suspend fun show(
        @Query("api_key") apiKey: String,
        @Path("id") id: Int,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String?
    ): HttpResponse

    @GET("tv/{show_id}/season/{season_id}")
    suspend fun showSeason(
        @Query("api_key") apiKey: String,
        @Path("show_id") showId: Int,
        @Path("season_id") seasonId: Int,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String?
    ): HttpResponse

}