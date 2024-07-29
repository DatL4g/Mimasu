package dev.datlag.mimasu.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.mimasu.tmdb.model.FindSource
import io.ktor.client.statement.HttpResponse

interface Find {

    @GET("find/{id}")
    suspend fun find(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("external_source") source: String,
        @Query("language") language: String,
    ): HttpResponse
}