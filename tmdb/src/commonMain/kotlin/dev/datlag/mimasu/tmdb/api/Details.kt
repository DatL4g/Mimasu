package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.mimasu.core.serializer.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.TMDB.Companion.ORIGINAL_IMAGE
import dev.datlag.mimasu.tmdb.TMDB.Companion.W500_IMAGE
import io.ktor.client.statement.HttpResponse
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

interface Details {

    @GET("movie/{id}")
    suspend fun movie(
        @Query("api_key") apiKey: String,
        @Path("id") id: Int,
        @Query("language") language: String,
    ): HttpResponse

    @Serializable
    data class Movie(
        @SerialName("id") val id: Int,
        @SerialName("adult") val adult: Boolean = false,
        @SerialName("backdrop_path") private val backdropPath: String? = null,
        @SerialName("belongs_to_collection") private val belongsToCollection: Collection? = null,
        @SerialName("budget") val budget: Long? = null,
        @SerialName("genres") val genres: SerializableImmutableSet<Genre> = persistentSetOf(),
        @SerialName("homepage") val homepage: String? = null,
        @SerialName("overview") val overview: String? = null,
        @SerialName("original_title") val originalTitle: String? = null,
        @SerialName("title") val title: String,
        @SerialName("poster_path") private val posterPath: String? = null,
    ) {
        @Transient
        val backdropPicture: String? = backdropPath?.ifBlank {
            null
        }?.let { "$ORIGINAL_IMAGE$it" } ?: belongsToCollection?.backdropPicture

        @Transient
        val backdropPictureW500: String? = backdropPath?.ifBlank {
            null
        }?.let { "$W500_IMAGE$it" } ?: belongsToCollection?.backdropPictureW500

        @Transient
        val posterPicture: String? = posterPath?.ifBlank {
            null
        }?.let { "$ORIGINAL_IMAGE$it" } ?: belongsToCollection?.posterPicture

        @Transient
        val posterPictureW500: String? = posterPath?.ifBlank {
            null
        }?.let { "$W500_IMAGE$it" } ?: belongsToCollection?.posterPictureW500

        @Transient
        val alternativeTitle: String? = if (originalTitle.equals(title, ignoreCase = true)) {
            null
        } else {
            originalTitle?.ifBlank { null }
        }

        @Serializable
        data class Genre(
            @SerialName("id") val id: Int,
            @SerialName("name") val name: String
        )

        @Serializable
        data class Collection(
            @SerialName("id") val id: Int,
            @SerialName("name") val name: String,
            @SerialName("poster_path") private val posterPath: String? = null,
            @SerialName("backdrop_path") private val backdropPath: String? = null,
        ) {
            @Transient
            val backdropPicture: String? = backdropPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

            @Transient
            val backdropPictureW500: String? = backdropPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }

            @Transient
            val posterPicture: String? = posterPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

            @Transient
            val posterPictureW500: String? = posterPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }
        }
    }
}