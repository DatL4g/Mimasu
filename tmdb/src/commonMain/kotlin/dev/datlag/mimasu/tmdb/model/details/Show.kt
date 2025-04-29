package dev.datlag.mimasu.tmdb.model.details

import dev.datlag.mimasu.tmdb.model.HasBackdrop
import dev.datlag.mimasu.tmdb.model.HasPoster
import dev.datlag.tooling.scopeCatching
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Show(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("backdrop_path") override val backdropSource: String? = null,

    @SerialName("episode_run_time") val episodeRuntime: Set<Int> = emptySet(),
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("genres") val genres: Set<Genre> = emptySet(),
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("id") val id: Int,
    @SerialName("in_production") val inProduction: Boolean = true,
    @SerialName("languages") val languages: Set<String> = emptySet(),
    @SerialName("last_air_date") val lastAirDate: String? = null,

    @SerialName("name") val name: String,
    @SerialName("next_episode_to_air") val nextEpisodeToAir: String? = null,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int = 0,
    @SerialName("number_of_seasons") val numberOfSeasons: Int = 0,
    @SerialName("origin_country") val originCountry: Set<String> = emptySet(),
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("poster_path") override val posterSource: String? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("vote_count") val voteCount: Int = 0,
) : HasBackdrop, HasPoster {

    @Transient
    val lastAirLocalDate = lastAirDate?.ifBlank { null }?.let { scopeCatching {
        LocalDate.parse(it)
    }.getOrNull() }

    @Serializable
    data class Genre(
        @SerialName("id") val id: Int,
        @SerialName("name") val name: String
    )
}