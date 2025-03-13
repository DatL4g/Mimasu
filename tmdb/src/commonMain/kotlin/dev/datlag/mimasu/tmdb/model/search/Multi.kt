package dev.datlag.mimasu.tmdb.model.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Multi(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("backdrop_path") private val backdropPath: String? = null,
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") private val posterPath: String? = null,
    @SerialName("media_type") val mediaType: String? = null,
    @SerialName("genre_ids") val genreIds: Set<Int> = emptySet(),
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("video") val video: Boolean = true,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("vote_count") val voteCount: Int = 0
)
