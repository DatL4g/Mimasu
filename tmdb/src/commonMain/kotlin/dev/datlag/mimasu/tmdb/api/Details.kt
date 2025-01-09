package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.mimasu.core.serializer.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.TMDB.Companion.ORIGINAL_IMAGE
import dev.datlag.mimasu.tmdb.TMDB.Companion.W500_IMAGE
import io.ktor.client.statement.HttpResponse
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.roundToInt

interface Details {

    @GET("movie/{id}")
    suspend fun movie(
        @Query("api_key") apiKey: String,
        @Path("id") id: Int,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String? = null
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
        @SerialName("original_language") val originalLanguage: String? = null,
        @SerialName("original_title") val originalTitle: String? = null,
        @SerialName("title") val title: String,
        @SerialName("poster_path") private val posterPath: String? = null,
        @SerialName("imdb_id") val imdbId: String? = null,
        @SerialName("runtime") val runtime: Int = 0,
        @SerialName("tagline") private val _tagline: String? = null,
        @SerialName("videos") private val videos: VideoResult? = null,
        @SerialName("status") val status: String? = null,
        @SerialName("popularity") val popularity: Float = 0F,
        @SerialName("revenue") val revenue: Long? = null,
        @SerialName("vote_average") val average: Float = 0F,
        @SerialName("vote_count") val count: Int = 0,
        @SerialName("credits") val credits: Credits? = null
    ) {
        @Transient
        val tagline: String? = _tagline?.ifBlank { null }

        @Transient
        val averageScore: Int = (average * 10F).roundToInt()

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

        @Transient
        val trailer: ImmutableSet<VideoResult.Video> = videos?.results?.filter {
            it.type.equals("Trailer", ignoreCase = true)
        }?.let { list ->
            list.filter { it.official }.ifEmpty { list }
        }?.toImmutableSet() ?: persistentSetOf()

        fun youtubeTrailer(language: String, country: String): VideoResult.Video? {
            val youtubeVideos = trailer.filter {
                it.site.equals("youtube", ignoreCase = true)
            }.ifEmpty { return null }.sortedByDescending { it.size }

            return youtubeVideos.firstOrNull {
                it.language.equals(language, ignoreCase = true)
            } ?: youtubeVideos.firstOrNull {
                it.country.equals(country, ignoreCase = true)
            } ?: youtubeVideos.firstOrNull {
                it.language.equals(originalLanguage ?: "", ignoreCase = true)
            }
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

        @Serializable
        data class VideoResult(
            @SerialName("results") val results: SerializableImmutableSet<Video>
        ) {

            @Serializable
            data class Video(
                @SerialName("key") val key: String,
                @SerialName("site") val site: String? = null,
                @SerialName("size") val size: Int = 0,
                @SerialName("type") val type: String? = null,
                @SerialName("official") val official: Boolean = false,
                @SerialName("iso_639_1") val language: String? = null,
                @SerialName("iso_3166_1") val country: String? = null
            )
        }

        @Serializable
        data class Credits(
            @SerialName("cast") val cast: SerializableImmutableSet<Cast> = persistentSetOf(),
            @SerialName("crew") val crew: SerializableImmutableSet<Crew> = persistentSetOf()
        ) {

            @Serializable
            data class Cast(
                @SerialName("adult") val adult: Boolean = true,
                @SerialName("gender") val gender: Int = 0,
                @SerialName("id") val id: Int = 0,
                @SerialName("name") private val _name: String? = null,
                @SerialName("original_name") private val _originalName: String? = null,
                @SerialName("character") private val _character: String? = null,
                @SerialName("profile_path") private val picture: String? = null,
                @SerialName("known_for_department") private val _knownForDepartment: String? = null
            ) {
                @Transient
                val name: String? = _name?.ifBlank { null }

                @Transient
                val originalName: String? = _originalName?.ifBlank { null }

                @Transient
                val character: String? = _character?.ifBlank { null }

                @Transient
                val profilePicture: String? = picture?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

                @Transient
                val profilePictureW500: String? = picture?.ifBlank { null }?.let { "$W500_IMAGE$it" }

                @Transient
                val knownForDepartment: String? = _knownForDepartment?.ifBlank { null }
            }

            @Serializable
            data class Crew(
                @SerialName("adult") val adult: Boolean = true,
                @SerialName("gender") val gender: Int = 0,
                @SerialName("id") val id: Int = 0,
                @SerialName("name") val name: String? = null,
                @SerialName("original_name") val originalName: String? = null,
                @SerialName("profile_path") private val picture: String? = null
            ) {
                @Transient
                val profilePicture: String? = picture?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

                @Transient
                val profilePictureW500: String? = picture?.ifBlank { null }?.let { "$W500_IMAGE$it" }
            }
        }
    }
}