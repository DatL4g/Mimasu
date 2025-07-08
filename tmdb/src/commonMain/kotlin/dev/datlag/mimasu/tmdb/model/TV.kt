package dev.datlag.mimasu.tmdb.model

import dev.datlag.tooling.scopeCatching
import kotlinx.datetime.LocalDate
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class TV(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("backdrop_path") override val backdropSource: String? = null,
    @SerialName("id") override val id: Int,
    @SerialName("name") val name: String,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("overview") private val _overview: String? = null,
    @SerialName("poster_path") override val posterSource: String? = null,
    @SerialName("media_type") @EncodeDefault(EncodeDefault.Mode.ALWAYS) override val mediaType: String? = "tv",
    @SerialName("genre_ids") val genreIds: Set<Int> = emptySet(),
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("origin_country") val originCountry: Set<String> = emptySet()
): Response, HasBackdrop, HasPoster {

    @Transient
    val overview: String? = _overview?.trim()?.ifBlank { null }

    @Transient
    val firstAirLocalDate = firstAirDate?.ifBlank { null }?.let { scopeCatching {
        LocalDate.parse(it)
    }.getOrNull() }
}