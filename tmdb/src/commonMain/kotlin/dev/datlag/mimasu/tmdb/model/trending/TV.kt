package dev.datlag.mimasu.tmdb.model.trending

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TV(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("backdrop_path") private val backdropPath: String? = null,
    @SerialName("id") override val id: Int,
    @SerialName("name") val name: String,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") private val posterPath: String? = null,
    @SerialName("media_type") override val mediaType: String? = "tv",
    @SerialName("genre_ids") val genreIds: Set<Int> = emptySet(),
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("origin_country") val originCountry: Set<String> = emptySet()
): Response
