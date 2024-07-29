package dev.datlag.mimasu.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

// ToDo("replace with [Window]")
interface Trending {

    @GET("trending/all/{window}")
    suspend fun all(
        @Header("Authorization") authorization: String,
        @Path("window") window: String,
        @Query("language") language: String
    ): HttpResponse

    @GET("trending/movie/{window}")
    suspend fun movies(
        @Header("Authorization") authorization: String,
        @Path("window") window: String,
        @Query("language") language: String
    ): HttpResponse

    @GET("trending/person/{window}")
    suspend fun people(
        @Header("Authorization") authorization: String,
        @Path("window") window: String,
        @Query("language") language: String
    ): HttpResponse

    @GET("trending/tv/{window}")
    suspend fun tv(
        @Header("Authorization") authorization: String,
        @Path("window") window: String,
        @Query("language") language: String
    ): HttpResponse
}