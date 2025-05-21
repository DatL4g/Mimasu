package dev.datlag.mimasu.tmdb.model.details

import dev.datlag.mimasu.tmdb.model.HasLogo
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
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("watch/providers") val watchProviders: WatchProviders? = null
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

    @Serializable
    data class WatchProviders(
        @SerialName("results") val results: Map<String, Providers> = emptyMap()
    ) {

        fun providerFor(locale: String) = (results[locale] ?: results[locale.uppercase()])?.takeUnless { it.isEmpty() }

        @Serializable
        data class Providers(
            @SerialName("link") val link: String? = null,
            @SerialName("flatrate") val flatrate: Set<Info> = emptySet(),
            @SerialName("buy") val buy: Set<Info> = emptySet(),
            @SerialName("ads") val ads: Set<Info> = emptySet(),
            @SerialName("free") val free: Set<Info> = emptySet(),
            @SerialName("rent") val rent: Set<Info> = emptySet(),
        ) {

            fun isEmpty(): Boolean {
                return link.isNullOrBlank() && !hasProviders()
            }

            fun hasProviders(): Boolean {
                return flatrate.isNotEmpty()
                        || buy.isNotEmpty()
                        || rent.isNotEmpty()
                        || free.isNotEmpty()
                        || ads.isNotEmpty()
            }

            @Serializable
            data class Info(
                @SerialName("logo_path") override val logoSource: String? = null,
                @SerialName("provider_id") val providerId: Int = 0,
                @SerialName("provider_name") val providerName: String,
                @SerialName("display_priority") val displayPriority: Int = 0
            ): HasLogo
        }
    }
}
