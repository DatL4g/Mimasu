package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.RequestType
import io.ktor.client.statement.HttpResponse

interface MovieLists {

    @GET("movie/now_playing")
    suspend fun nowPlaying(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("region") region: String?
    ): HttpResponse

    @GET("movie/popular")
    suspend fun popular(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("region") region: String?
    ): HttpResponse

    @GET("movie/top_rated")
    suspend fun topRated(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("region") region: String?
    ): HttpResponse

    @GET("movie/upcoming")
    suspend fun upcoming(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("region") region: String?
    ): HttpResponse
}