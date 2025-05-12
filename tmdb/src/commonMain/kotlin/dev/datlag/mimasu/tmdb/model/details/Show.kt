package dev.datlag.mimasu.tmdb.model.details

import dev.datlag.mimasu.tmdb.model.HasBackdrop
import dev.datlag.mimasu.tmdb.model.HasLogo
import dev.datlag.mimasu.tmdb.model.HasPoster
import dev.datlag.tooling.scopeCatching
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.roundToInt

@Serializable
data class Show(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("backdrop_path") override val backdropSource: String? = null,

    @SerialName("episode_run_time") val episodeRuntime: Set<Int> = emptySet(),
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("genres") val genres: Set<Genre> = emptySet(),
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("id") val id: Int,
    @SerialName("imdb_id") private val _imdbId: String? = null,
    @SerialName("in_production") val inProduction: Boolean = true,
    @SerialName("languages") val languages: Set<String> = emptySet(),
    @SerialName("last_air_date") val lastAirDate: String? = null,

    @SerialName("name") val name: String,
    // @SerialName("next_episode_to_air") val nextEpisodeToAir: String? = null, // not a string
    @SerialName("number_of_episodes") val numberOfEpisodes: Int = 0,
    @SerialName("number_of_seasons") val numberOfSeasons: Int = 0,
    @SerialName("origin_country") val originCountry: Set<String> = emptySet(),
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("poster_path") override val posterSource: String? = null,
    @SerialName("production_companies") val productionCompanies: Set<ProductionCompany> = emptySet(),
    @SerialName("production_countries") val productionCountries: Set<ProductionCountries> = emptySet(),
    @SerialName("seasons") val seasons: Set<Season> = emptySet(),
    @SerialName("status") @Serializable(Status.Serializer::class) val status: Status? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("original_tagline") val originalTagline: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("external_ids") val externalIDs: ExternalIDs? = null,
) : HasBackdrop, HasPoster {

    @Transient
    val imdbId: String? = _imdbId?.ifBlank { null } ?: externalIDs?.imdbId?.ifBlank { null }

    @Transient
    val displaySeasons = seasons.filter { it.episodeCount > 0 }

    @Transient
    val runtimeAverage: Int = episodeRuntime.filter { it > 0 }.let {
        if (it.isEmpty()) {
            0
        } else {
            val avg = scopeCatching { it.average() }.getOrNull() ?: return@let 0
            scopeCatching {
                avg.roundToInt()
            }.getOrNull() ?: avg.toInt()
        }
    }

    @Transient
    val firstAirLocalDate = firstAirDate?.ifBlank { null }?.let { scopeCatching {
        LocalDate.parse(it)
    }.getOrNull() }

    @Transient
    val lastAirLocalDate = lastAirDate?.ifBlank { null }?.let { scopeCatching {
        LocalDate.parse(it)
    }.getOrNull() }

    @Serializable
    data class Genre(
        @SerialName("id") val id: Int,
        @SerialName("name") val name: String
    )

    @Serializable
    data class ProductionCompany(
        @SerialName("id") val id: Int = 0,
        @SerialName("logo_path") override val logoSource: String? = null,
        @SerialName("name") val name: String,
        @SerialName("origin_country") val originCountry: String? = null
    ) : HasLogo

    @Serializable
    data class ProductionCountries(
        @SerialName("iso_3166_1") val iso: String? = null,
        @SerialName("name") val name: String? = null
    )

    @Serializable
    data class Season(
        @SerialName("air_date") val airDate: String? = null,
        @SerialName("episode_count") val episodeCount: Int = 0,
        @SerialName("id") val id: Int = 0,
        @SerialName("name") val name: String,
        @SerialName("overview") val overview: String? = null,
        @SerialName("poster_path") override val posterSource: String? = null,
        @SerialName("season_number") val seasonNumber: Int = 0,
        @SerialName("vote_average") val voteAverage: Float = 0F
    ) : HasPoster

    @Serializable
    sealed class Status : CharSequence {

        abstract val value: String

        override val length: Int
            get() = value.length

        override operator fun get(index: Int): Char {
            return value[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return value.subSequence(startIndex, endIndex)
        }

        override fun toString(): String {
            return value
        }

        @Serializable
        data object Returning : Status() {
            override val value: String = "Returning Series"
        }

        @Serializable
        data object Planned : Status() {
            override val value: String = "Planned"
        }

        @Serializable
        data object Pilot : Status() {
            override val value: String = "Pilot"
        }

        @Serializable
        data object InProduction : Status() {
            override val value: String = "In Production"
        }

        @Serializable
        data object Ended : Status() {
            override val value: String = "Ended"
        }

        @Serializable
        data object Canceled : Status() {
            override val value: String = "Canceled"
        }

        @Serializable
        data class Custom(override val value: String) : Status()

        companion object Serializer : KSerializer<Status?> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ShowStatus", PrimitiveKind.STRING)

            @OptIn(ExperimentalSerializationApi::class)
            override fun serialize(encoder: Encoder, value: Status?) {
                if (value == null || value.value.isBlank()) {
                    encoder.encodeNull()
                } else {
                    encoder.encodeNotNullMark()
                    encoder.encodeString(value.value)
                }
            }

            @OptIn(ExperimentalSerializationApi::class)
            override fun deserialize(decoder: Decoder): Status? {
                return if (decoder.decodeNotNullMark()) {
                    from(decoder.decodeString())
                } else {
                    decoder.decodeNull()
                }
            }

            fun from(value: String): Status = when {
                value.equals(Returning.value, ignoreCase = true) -> Returning
                value.equals(Planned.value, ignoreCase = true) -> Planned
                value.equals(Pilot.value, ignoreCase = true) -> Pilot
                value.equals(InProduction.value, ignoreCase = true) -> InProduction
                value.equals(Ended.value, ignoreCase = true) -> Ended
                value.equals(Canceled.value, ignoreCase = true) -> Canceled
                else -> Custom(value)
            }
        }
    }

    @Serializable
    data class ExternalIDs(
        @SerialName("id") val id: Int = 0,
        @SerialName("imdb_id") val imdbId: String? = null,
        @SerialName("wikidata_id") val wikidataId: String? = null,
        @SerialName("facebook_id") val facebookId: String? = null,
        @SerialName("instagram_id") val instagramId: String? = null,
        @SerialName("twitter_id") val twitterId: String? = null
    )
}