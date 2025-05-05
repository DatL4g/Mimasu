package dev.datlag.mimasu.tmdb.model.details

import dev.datlag.mimasu.tmdb.model.HasPoster
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Season(
    @SerialName("_id") val _id: String? = null,
    @SerialName("air_date") val airDate: String? = null,
    @SerialName("episodes") val episodes: Set<Episode> = emptySet(),
    @SerialName("name") val name: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("id") val id: Int = 0,
    @SerialName("poster_path") override val posterSource: String? = null,
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("vote_average") val voteAverage: Float = 0F
) : HasPoster {

    @Serializable
    data class Episode(
        @SerialName("air_date") val airDate: String? = null,
        @SerialName("episode_number") val episodeNumber: Int = 0,
        @SerialName("id") val id: Int = 0,
        @SerialName("name") val name: String? = null,
        @SerialName("overview") val overview: String? = null,
        @SerialName("runtime") private val _runtime: Int? = null,
        @SerialName("season_number") val seasonNumber: Int = 0,
        @SerialName("show_id") val showId: Int = 0,
        @SerialName("still_path") override val posterSource: String? = null,
    ) : HasPoster {

        @Transient
        val runtime = _runtime ?: 0
    }
}
