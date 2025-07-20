package dev.datlag.mimasu.tmdb.model.details

import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.model.HasLogo
import dev.datlag.mimasu.tmdb.model.HasPoster
import dev.datlag.mimasu.tmdb.model.details.Show.WatchProviders
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Season(
    @SerialName("_id") val _id: String? = null,
    @SerialName("air_date") val airDate: String? = null,
    @SerialName("episodes") val episodes: SerializableImmutableSet<Episode> = persistentSetOf(),
    @SerialName("name") val name: String? = null,
    @SerialName("overview") private val _overview: String? = null,
    @SerialName("id") val id: Int = 0,
    @SerialName("poster_path") override val posterSource: String? = null,
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("watch/providers") val watchProviders: WatchProviders? = null
) : HasPoster {

    @Transient
    val overview: String? = _overview?.trim()?.ifBlank { null }

    @Serializable
    data class Episode(
        @SerialName("air_date") val airDate: String? = null,
        @SerialName("episode_number") val episodeNumber: Int = 0,
        @SerialName("id") val id: Int = 0,
        @SerialName("name") val name: String? = null,
        @SerialName("overview") private val _overview: String? = null,
        @SerialName("runtime") private val _runtime: Int? = null,
        @SerialName("season_number") val seasonNumber: Int = 0,
        @SerialName("show_id") val showId: Int = 0,
        @SerialName("still_path") override val posterSource: String? = null,
    ) : HasPoster {

        @Transient
        val overview: String? = _overview?.trim()?.ifBlank { null }

        @Transient
        val runtime = _runtime ?: 0

        @Transient
        val identifier: Int = id.takeIf { it > 0 } ?: hashCode()
    }
}
