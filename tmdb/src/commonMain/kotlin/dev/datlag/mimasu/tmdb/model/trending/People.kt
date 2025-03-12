package dev.datlag.mimasu.tmdb.model.trending

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.jvm.JvmOverloads

@Serializable
@ConsistentCopyVisibility
data class People internal constructor(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("id") override val id: Int,
    @SerialName("name") val name: String,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("media_type") override val mediaType: String? = "people",
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("gender") val gender: Int = 0,
    @SerialName("known_for_department") val knownForDepartment: String? = null,
    @SerialName("profile_path") private val profilePath: String? = null,
    @SerialName("known_for") private val knownFor: Set<Response> = emptySet(),
): Response {

    @Transient
    val knownForMovie = knownFor.mapNotNull {
        when (it) {
            is Movie -> it
            is KnownFor -> it.asMovie(ignoreType = false)
            else -> null
        }
    }

    @Transient
    val knownForTV = knownFor.mapNotNull {
        when (it) {
            is TV -> it
            is KnownFor -> it.asTV(ignoreType = false)
            else -> null
        }
    }

    @Serializable
    data class KnownFor(
        @SerialName("adult") val adult: Boolean = true,
        @SerialName("backdrop_path") private val backdropPath: String? = null,
        @SerialName("id") override val id: Int,
        @SerialName("title") val title: String,
        @SerialName("original_language") val originalLanguage: String? = null,
        @SerialName("original_title") val originalTitle: String? = null,
        @SerialName("overview") val overview: String? = null,
        @SerialName("poster_path") private val posterPath: String? = null,
        @SerialName("media_type") override val mediaType: String? = null,
        @SerialName("genre_ids") val genreIds: Set<Int> = emptySet(),
        @SerialName("popularity") val popularity: Float = 0F,
        @SerialName("release_date") val releaseDate: String? = null,
        @SerialName("video") val video: Boolean = true,
        @SerialName("vote_average") val voteAverage: Float = 0F,
        @SerialName("vote_count") val voteCount: Int = 0
    ): Response {
        @JvmOverloads
        fun asMovie(ignoreType: Boolean = false): Movie? = when {
            ignoreType || mediaType.equals("movie", ignoreCase = true) -> {
                Movie(
                    adult = adult,
                    backdropPath = backdropPath,
                    id = id,
                    title = title,
                    originalLanguage = originalLanguage,
                    originalTitle = originalTitle,
                    overview = overview,
                    posterPath = posterPath,
                    mediaType = mediaType,
                    genreIds = genreIds,
                    popularity = popularity,
                    releaseDate = releaseDate,
                    video = video,
                    voteAverage = voteAverage,
                    voteCount = voteCount
                )
            }
            else -> null
        }

        @JvmOverloads
        fun asTV(ignoreType: Boolean = false): TV? = when {
            ignoreType || mediaType.equals("tv", ignoreCase = true) -> {
                TV(
                    adult = adult,
                    backdropPath = backdropPath,
                    id = id,
                    name = title,
                    originalLanguage = originalLanguage,
                    originalName = originalTitle,
                    overview = overview,
                    posterPath = posterPath,
                    mediaType = mediaType,
                    genreIds = genreIds,
                    popularity = popularity,
                    firstAirDate = releaseDate,
                    voteAverage = voteAverage,
                    voteCount = voteCount
                )
            }
            else -> null
        }
    }
}
