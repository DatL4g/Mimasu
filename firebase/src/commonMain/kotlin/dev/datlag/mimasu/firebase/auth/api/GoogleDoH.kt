package dev.datlag.mimasu.firebase.auth.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.mimasu.firebase.auth.model.GoogleDoHResponse
import io.ktor.client.HttpClient

interface GoogleDoH {

    @GET("resolve")
    suspend fun resolve(
        @Query("name") name: String,
        @Query("type") type: String
    ): GoogleDoHResponse

    companion object {
        const val BASE_URL = "https://dns.google/"

        fun create(client: HttpClient): GoogleDoH {
            return ktorfit {
                baseUrl(BASE_URL)
                httpClient(client)
            }.createGoogleDoH()
        }
    }
}