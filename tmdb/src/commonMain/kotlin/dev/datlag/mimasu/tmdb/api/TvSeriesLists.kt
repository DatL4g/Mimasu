package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

interface TvSeriesLists {

    /**
     * Get a list of TV shows airing today.
     */
    @GET("tv/airing_today")
    suspend fun airingToday(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("timezone") timezone: String
    ): HttpResponse

    /**
     * Get a list of TV shows that air in the next 7 days.
     */
    @GET("tv/on_the_air")
    suspend fun onTheAir(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("timezone") timezone: String
    ): HttpResponse

    /**
     * Get a list of TV shows ordered by popularity.
     */
    @GET("tv/popular")
    suspend fun popular(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse

    /**
     * Get a list of TV shows ordered by rating.
     */
    @GET("tv/top_rated")
    suspend fun topRated(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse

}