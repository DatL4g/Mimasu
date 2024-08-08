package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.mimasu.core.serializer.SerializableImmutableSet
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface WatchProviders {

    @GET("watch/providers/movie")
    suspend fun movie(
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): HttpResponse

    @GET("watch/providers/tv")
    suspend fun tv(
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): HttpResponse

    @Serializable
    data class Response(
        @SerialName("results") val results: SerializableImmutableSet<Provider>
    ) {
        @Serializable
        data class Provider(
            @SerialName("provider_id") val id: Int,
            @SerialName("provider_name") val name: String,
            @SerialName("logo_path") val logoPath: String? = null,
        )
    }
}