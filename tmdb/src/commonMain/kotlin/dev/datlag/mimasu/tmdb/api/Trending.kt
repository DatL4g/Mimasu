package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

// ToDo("replace with [Window]")
interface Trending {

    /**
     * Get the trending movies, TV shows and people.
     */
    @GET("trending/all/{window}")
    suspend fun all(
        @Query("api_key") apiKey: String,
        @Path("window") window: String,
        @Query("language") language: String
    ): HttpResponse

    /**
     * Get the trending movies on TMDB.
     */
    @GET("trending/movie/{window}")
    suspend fun movies(
        @Query("api_key") apiKey: String,
        @Path("window") window: String,
        @Query("language") language: String
    ): HttpResponse

    /**
     * Get the trending people on TMDB.
     */
    @GET("trending/person/{window}")
    suspend fun people(
        @Query("api_key") apiKey: String,
        @Path("window") window: String,
        @Query("language") language: String
    ): HttpResponse

    /**
     * Get the trending TV shows on TMDB.
     */
    @GET("trending/tv/{window}")
    suspend fun tv(
        @Query("api_key") apiKey: String,
        @Path("window") window: String,
        @Query("language") language: String
    ): HttpResponse
}