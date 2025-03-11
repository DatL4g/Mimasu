package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.RequestType
import dev.datlag.mimasu.tmdb.model.trending.TimeWindow
import io.ktor.client.statement.HttpResponse

interface Trending {

    @GET("trending/movie/{window}")
    suspend fun movies(
        @Query("api_key") apiKey: String,
        @Path("window") @RequestType(String::class) window: TimeWindow,
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse

    @GET("trending/person/{window}")
    suspend fun people(
        @Query("api_key") apiKey: String,
        @Path("window") @RequestType(String::class) window: TimeWindow,
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse

    @GET("trending/tv/{window}")
    suspend fun tv(
        @Query("api_key") apiKey: String,
        @Path("window") @RequestType(String::class) window: TimeWindow,
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse
}