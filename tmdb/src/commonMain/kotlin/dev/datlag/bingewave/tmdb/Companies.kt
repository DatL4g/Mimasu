package dev.datlag.bingewave.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

interface Companies {

    @GET("company/{id}")
    suspend fun details(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    ): HttpResponse

    @GET("company/{id}/alternative_names")
    suspend fun alternativeNames(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    ): HttpResponse

    @GET("company/{id}/images")
    suspend fun images(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    ): HttpResponse
}