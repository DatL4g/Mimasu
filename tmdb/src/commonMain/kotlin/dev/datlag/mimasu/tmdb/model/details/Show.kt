package dev.datlag.mimasu.tmdb.model.details

import dev.datlag.mimasu.core.serialization.SerializableImmutableMap
import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.model.HasBackdrop
import dev.datlag.mimasu.tmdb.model.HasKana
import dev.datlag.mimasu.tmdb.model.HasLogo
import dev.datlag.mimasu.tmdb.model.HasPoster
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.tooling.scopeCatching
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
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

    @SerialName("episode_run_time") val episodeRuntime: SerializableImmutableSet<Int> = persistentSetOf(),
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("genres") val genres: SerializableImmutableSet<Genre> = persistentSetOf(),
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("id") val id: Int,
    @SerialName("imdb_id") private val _imdbId: String? = null,
    @SerialName("in_production") val inProduction: Boolean = true,
    @SerialName("languages") val languages: SerializableImmutableSet<String> = persistentSetOf(),
    @SerialName("last_air_date") val lastAirDate: String? = null,

    @SerialName("name") val name: String,
    // @SerialName("next_episode_to_air") val nextEpisodeToAir: String? = null, // not a string
    @SerialName("number_of_episodes") val numberOfEpisodes: Int = 0,
    @SerialName("number_of_seasons") val numberOfSeasons: Int = 0,
    @SerialName("origin_country") val originCountry: SerializableImmutableSet<String> = persistentSetOf(),
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("overview") private val _overview: String? = null,
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("poster_path") override val posterSource: String? = null,
    @SerialName("production_companies") val productionCompanies: SerializableImmutableSet<ProductionCompany> = persistentSetOf(),
    @SerialName("production_countries") val productionCountries: SerializableImmutableSet<ProductionCountries> = persistentSetOf(),
    @SerialName("seasons") val seasons: SerializableImmutableSet<Season> = persistentSetOf(),
    @SerialName("status") @Serializable(Status.Serializer::class) val status: Status? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("original_tagline") val originalTagline: String? = null,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("external_ids") val externalIDs: ExternalIDs? = null,
    @SerialName("watch/providers") val watchProviders: WatchProviders? = null
) : HasBackdrop, HasPoster, HasKana {

    @Transient
    override val kanaSource: String? = name.ifBlank { null }

    @Transient
    override val kanaBackupSource: String? = originalName?.ifBlank { null }

    @Transient
    val imdbId: String? = _imdbId?.ifBlank { null } ?: externalIDs?.imdbId?.ifBlank { null }

    @Transient
    val overview: String? = _overview?.trim()?.ifBlank { null }

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

    fun asCommon(): TV = TV(
        adult = adult,
        backdropSource = backdropSource,
        id = id,
        name = name,
        originalLanguage = originalLanguage,
        originalName = originalName,
        _overview = overview,
        popularity = popularity,
        posterSource = posterSource,
        genreIds = genres.map { it.id }.toImmutableSet(),
        firstAirDate = firstAirDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        originCountry = originCountry
    )

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
        abstract val discoverValue: Int

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
            override val discoverValue: Int = 0
        }

        @Serializable
        data object Planned : Status() {
            override val value: String = "Planned"
            override val discoverValue: Int = 1
        }

        @Serializable
        data object Pilot : Status() {
            override val value: String = "Pilot"
            override val discoverValue: Int = 5
        }

        @Serializable
        data object InProduction : Status() {
            override val value: String = "In Production"
            override val discoverValue: Int = 2
        }

        @Serializable
        data object Ended : Status() {
            override val value: String = "Ended"
            override val discoverValue: Int = 3
        }

        @Serializable
        data object Canceled : Status() {
            override val value: String = "Canceled"
            override val discoverValue: Int = 4
        }

        @Serializable
        data class Custom(
            override val value: String,
            override val discoverValue: Int = value.toIntOrNull() ?: 6
        ) : Status()

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
                else -> when (value.toIntOrNull()) {
                    Returning.discoverValue -> Returning
                    Planned.discoverValue -> Planned
                    Pilot.discoverValue -> Pilot
                    InProduction.discoverValue -> InProduction
                    Ended.discoverValue -> Ended
                    Canceled.discoverValue -> Canceled
                    else -> Custom(value)
                }
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

    @Serializable
    data class WatchProviders(
        @SerialName("results") val results: SerializableImmutableMap<String, Providers> = persistentMapOf()
    ) {

        fun providerFor(locale: String) = (results[locale] ?: results[locale.uppercase()])?.takeUnless { it.isEmpty() }

        @Serializable
        data class Providers(
            @SerialName("link") val link: String? = null,
            @SerialName("flatrate") val flatrate: SerializableImmutableSet<Info> = persistentSetOf(),
            @SerialName("buy") val buy: SerializableImmutableSet<Info> = persistentSetOf(),
            @SerialName("ads") val ads: SerializableImmutableSet<Info> = persistentSetOf(),
            @SerialName("free") val free: SerializableImmutableSet<Info> = persistentSetOf(),
            @SerialName("rent") val rent: SerializableImmutableSet<Info> = persistentSetOf(),
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