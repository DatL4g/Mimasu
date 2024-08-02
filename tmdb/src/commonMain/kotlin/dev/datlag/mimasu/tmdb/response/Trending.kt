package dev.datlag.mimasu.tmdb.response

import dev.datlag.mimasu.core.serializer.SerializableImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

sealed interface Trending {

    @Serializable
    data class All(
        @SerialName("page") val page: Int = 1,
        @SerialName("results") val results: SerializableImmutableSet<Result>,
        @SerialName("total_pages") val totalPages: Int = page,
        @SerialName("total_results") val totalResults: Int = results.size,
    ) : Trending {

        /**
         * @param _title used for movie
         * @param _originalTitle used for movie
         * @param _name used for tv and person
         * @param _originalName used for tv and person
         *
         * @param _releaseDate used for movie
         * @param _firstAirDate used for tv
         */
        @Serializable
        data class Result(
            @SerialName("backdrop_path") val backdropPath: String? = null,
            @SerialName("id") val id: Int,
            @SerialName("title") private val _title: String? = null,
            @SerialName("original_title") private val _originalTitle: String? = null,
            @SerialName("name") private val _name: String? = null,
            @SerialName("original_name") private val _originalName: String? = null,
            @SerialName("overview") val overview: String? = null,
            @SerialName("poster_path") val posterPath: String? = null,
            @SerialName("media_type") val mediaType: String? = null, // ToDo("create type serializer")
            @SerialName("adult") val adult: Boolean = false,
            @SerialName("original_language") val originalLanguage: String? = null,
            @SerialName("genre_ids") val genreIds: SerializableImmutableSet<Int> = persistentSetOf(),
            @SerialName("popularity") val popularity: Float? = null,
            @SerialName("release_date") private val _releaseDate: String? = null, // ToDo("create date serializer")
            @SerialName("first_air_date") private val _firstAirDate: String? = null, // ToDo("create date serializer")
            @SerialName("gender") val gender: Int = -1,
            @SerialName("known_for_department") val knownForDepartment: String? = null,
            @SerialName("profile_path") val profilePath: String? = null,
            @SerialName("video") val video: Boolean = false,
            @SerialName("vote_average") val voteAverage: Float? = null,
            @SerialName("vote_count") val voteCount: Int = 0,
            @SerialName("origin_country") val originCountry: SerializableImmutableSet<String> = persistentSetOf(),
            @SerialName("known_for") val knownFor: SerializableImmutableSet<Result> = persistentSetOf()
        ) {
            @Transient
            val originalTitle: String? = _originalTitle?.ifBlank { null } ?: _originalName?.ifBlank { null }

            @Transient
            val title: String? = _title?.ifBlank { null } ?: _name?.ifBlank { null } ?: originalTitle

            @Transient
            val releaseDate: String? = _releaseDate?.ifBlank { null } ?: _firstAirDate?.ifBlank { null }


        }
    }
}