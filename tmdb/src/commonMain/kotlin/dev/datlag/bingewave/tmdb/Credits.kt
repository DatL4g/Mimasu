package dev.datlag.bingewave.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

interface Credits {

    @GET("credit/{id}")
    suspend fun details(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): HttpResponse
}