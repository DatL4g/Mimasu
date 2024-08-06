package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.mimasu.core.serializer.ImmutableSetSerializer
import dev.datlag.mimasu.core.serializer.SerializableImmutableSet
import io.ktor.client.statement.HttpResponse
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse

    /**
     * Get the trending people on TMDB.
     */
    @GET("trending/person/{window}")
    suspend fun people(
        @Query("api_key") apiKey: String,
        @Path("window") window: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse

    /**
     * Get the trending TV shows on TMDB.
     */
    @GET("trending/tv/{window}")
    suspend fun tv(
        @Query("api_key") apiKey: String,
        @Path("window") window: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): HttpResponse

    @Serializable
    data class Response(
        @SerialName("page") val page: Int = 1,
        @SerialName("results") @Serializable(MediaSerializer::class) val results: SerializableImmutableSet<Media>,
        @SerialName("total_pages") val totalPages: Int = page,
        @SerialName("total_results") val totalResults: Int = results.size,
    ) {
        @Serializable
        sealed class Media {
            abstract val id: Int
            abstract val mediaType: String?

            @Serializable
            @SerialName("movie")
            data class Movie(
                @SerialName("id") override val id: Int,
                @SerialName("media_type") override val mediaType: String? = "movie",
                @SerialName("title") val title: String,
                @SerialName("original_title") val originalTitle: String? = null,
                @SerialName("backdrop_path") val backdropPath: String? = null,
                @SerialName("overview") val overview: String? = null,
                @SerialName("poster_path") val posterPath: String? = null,
                @SerialName("adult") val adult: Boolean = false,
                @SerialName("original_language") val originalLanguage: String? = null,
                @SerialName("genre_ids") val genreIds: SerializableImmutableSet<Int> = persistentSetOf(),
                @SerialName("video") val video: Boolean = false,
                @SerialName("vote_average") val voteAverage: Float = 0F,
                @SerialName("vote_count") val voteCount: Int = 0
            ) : Media() {
                @Transient
                val backdropPicture: String? = backdropPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

                @Transient
                val backdropPictureW500: String? = backdropPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }

                @Transient
                val posterPicture: String? = posterPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

                @Transient
                val posterPictureW500: String? = posterPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }

                @Transient
                val alternativeTitle: String? = if (originalTitle.equals(title, ignoreCase = true)) {
                    null
                } else {
                    originalTitle
                }
            }

            @Serializable
            @SerialName("tv")
            data class TV(
                @SerialName("id") override val id: Int,
                @SerialName("media_type") override val mediaType: String? = "tv",
                @SerialName("name") val name: String,
                @SerialName("original_name") val originalName: String? = null,
                @SerialName("backdrop_path") val backdropPath: String? = null,
                @SerialName("overview") val overview: String? = null,
                @SerialName("poster_path") val posterPath: String? = null,
                @SerialName("adult") val adult: Boolean = false,
                @SerialName("original_language") val originalLanguage: String? = null,
                @SerialName("genre_ids") val genreIds: SerializableImmutableSet<Int> = persistentSetOf(),
                @SerialName("vote_average") val voteAverage: Float = 0F,
                @SerialName("vote_count") val voteCount: Int = 0
            ) : Media() {
                @Transient
                val backdropPicture: String? = backdropPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

                @Transient
                val backdropPictureW500: String? = backdropPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }

                @Transient
                val posterPicture: String? = posterPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

                @Transient
                val posterPictureW500: String? = posterPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }

                @Transient
                val alternativeName: String? = if (originalName.equals(name, ignoreCase = true)) {
                    null
                } else {
                    originalName
                }
            }

            @Serializable
            @SerialName("person")
            data class Person(
                @SerialName("id") override val id: Int,
                @SerialName("media_type") override val mediaType: String? = "movie",
                @SerialName("name") val name: String,
                @SerialName("original_name") val originalName: String? = null,
                @SerialName("adult") val adult: Boolean = false,
                @SerialName("gender") val gender: Int = 0,
                @SerialName("known_for_department") val knownForDepartment: String? = null,
                @SerialName("profile_path") val profilePath: String? = null,
                @SerialName("known_for") @Serializable(MediaSerializer::class) val knownFor: SerializableImmutableSet<Media>
            ) : Media() {
                @Transient
                val profilePicture: String? = profilePath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

                @Transient
                val profilePictureW500: String? = profilePath?.ifBlank { null }?.let { "$W500_IMAGE$it" }
            }

            companion object Serializer : JsonContentPolymorphicSerializer<Media>(Media::class) {
                private const val ORIGINAL_IMAGE = "https://image.tmdb.org/t/p/original/"
                private const val W500_IMAGE = "https://image.tmdb.org/t/p/w500/"

                override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Media> {
                    val mediaType = element.jsonObject["media_type"]?.jsonPrimitive?.content
                    return when {
                        mediaType.equals("movie", ignoreCase = true) -> Movie.serializer()
                        mediaType.equals("tv", ignoreCase = true) -> TV.serializer()
                        mediaType.equals("person", ignoreCase = true) || mediaType.equals("people", ignoreCase = true) -> Person.serializer()
                        else -> throw IllegalArgumentException("Got media_type \"$mediaType\", expected [movie, tv]")
                    }
                }
            }
        }

        companion object MediaSerializer : JsonTransformingSerializer<SerializableImmutableSet<Media>>(ImmutableSetSerializer(Media.Serializer))
    }
}