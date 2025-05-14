package dev.datlag.mimasu.tmdb.model.details

import dev.datlag.mimasu.tmdb.model.HasBackdrop
import dev.datlag.mimasu.tmdb.model.HasPoster
import dev.datlag.mimasu.tmdb.model.HasLogo
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.Response
import dev.datlag.tooling.scopeCatching
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@Serializable
data class Movie(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("backdrop_path") override val backdropSource: String? = null,
    // @SerialName("belongs_to_collection") val belongsToCollection: String? = null, // is not string
    @SerialName("budget") val budget: Int = 0,
    @SerialName("genres") val genres: Set<Genre> = emptySet(),
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("id") val id: Int,
    @SerialName("imdb_id") private val _imdbId: String? = null,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("poster_path") override val posterSource: String? = null,
    @SerialName("production_companies") val productionCompanies: Set<ProductionCompany> = emptySet(),
    @SerialName("production_countries") val productionCountries: Set<ProductionCountries> = emptySet(),
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("revenue") val revenue: Int = 0,
    @SerialName("runtime") val runtime: Int = 0,
    @SerialName("spoken_languages") val spokenLanguages: Set<SpokenLanguage> = emptySet(),
    @SerialName("status") @Serializable(Status.Serializer::class) val status: Status? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("original_tagline") val originalTagline: String? = null,
    @SerialName("title") val title: String,
    @SerialName("video") val video: Boolean = true,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("credits") val credits: Credits? = null,
    @SerialName("external_ids") val externalIDs: ExternalIDs? = null,
    @SerialName("watch/providers") val watchProviders: WatchProviders? = null
) : HasBackdrop, HasPoster {

    @Transient
    val imdbId: String? = _imdbId?.ifBlank { null } ?: externalIDs?.imdbId?.ifBlank { null }

    @Transient
    val releaseLocalDate = releaseDate?.ifBlank { null }?.let { scopeCatching {
        LocalDate.parse(it)
    }.getOrNull() }

    fun asCommon(): CommonMovie = CommonMovie(
        adult = adult,
        backdropSource = backdropSource,
        id = id,
        title = title,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterSource = posterSource,
        releaseDate = releaseDate,
        genreIds = genres.map { it.id }.toImmutableSet(),
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount
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
    ): HasLogo

    @Serializable
    data class ProductionCountries(
        @SerialName("iso_3166_1") val iso: String? = null,
        @SerialName("name") val name: String? = null
    )

    @Serializable
    data class SpokenLanguage(
        @SerialName("english_name") val englishName: String? = null,
        @SerialName("iso_639_1") val iso: String? = null,
        @SerialName("name") val name: String
    )

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
        data object Rumored : Status() {
            override val value: String = "Rumored"
        }

        @Serializable
        data object Planned : Status() {
            override val value: String = "Planned"
        }

        @Serializable
        data object InProduction : Status() {
            override val value: String = "In Production"
        }

        @Serializable
        data object PostProduction : Status() {
            override val value: String = "Post Production"
        }

        @Serializable
        data object Released : Status() {
            override val value: String = "Released"
        }

        @Serializable
        data object Canceled : Status() {
            override val value: String = "Canceled"
        }

        @Serializable
        data class Custom(override val value: String) : Status()

        companion object Serializer : KSerializer<Status?> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MovieStatus", PrimitiveKind.STRING)

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
                value.equals(Rumored.value, ignoreCase = true) -> Rumored
                value.equals(Planned.value, ignoreCase = true) -> Planned
                value.equals(InProduction.value, ignoreCase = true) -> InProduction
                value.equals(PostProduction.value, ignoreCase = true) -> PostProduction
                value.equals(Released.value, ignoreCase = true) -> Released
                value.equals(Canceled.value, ignoreCase = true) -> Canceled
                else -> Custom(value)
            }
        }
    }

    @Serializable
    data class Credits(
        @SerialName("cast") private val _cast: Set<Cast> = emptySet(),
        @SerialName("crew") private val _crew: Set<Crew> = emptySet()
    ) {

        @Transient
        val cast = _cast.distinctBy { it.id }.sortedWith(compareBy<Cast> { it.order }.thenBy { it.popularity })

        @Transient
        val crew = _crew.distinctBy { it.id }.sortedBy { it.popularity }

        @Serializable
        data class Cast(
            @SerialName("adult") val adult: Boolean = true,
            @SerialName("id") val id: Int,
            @SerialName("name") val name: String,
            @SerialName("original_name") val originalName: String? = null,
            @SerialName("popularity") val popularity: Float = 0F,
            @SerialName("gender") val gender: Int = 0,
            @SerialName("known_for_department") val knownForDepartment: String? = null,
            @SerialName("profile_path") override val logoSource: String? = null,
            @SerialName("known_for") private val knownFor: Set<Response> = emptySet(),
            @SerialName("cast_id") val castId: Int = 0,
            @SerialName("character") private val _character: String? = null,
            @SerialName("credit_id") val creditId: String? = null,
            @SerialName("order") val order: Int = 0
        ) : HasLogo {

            @Transient
            val character : String? = _character?.ifBlank { null }
                ?.replace("($knownForDepartment)", "", ignoreCase = true)
                ?.replace("(voice)", "", ignoreCase = true)
                ?.trim()
                ?.takeUnless { it.equals("self", ignoreCase = true) }
                ?.ifBlank { null }

            @Transient
            val isFemale = gender == 1

            @Transient
            val isMale = gender == 2

            @Transient
            val isNonBinary = gender == 3
        }

        @Serializable
        data class Crew(
            @SerialName("adult") val adult: Boolean = true,
            @SerialName("id") val id: Int,
            @SerialName("name") val name: String,
            @SerialName("original_name") val originalName: String? = null,
            @SerialName("popularity") val popularity: Float = 0F,
            @SerialName("gender") val gender: Int = 0,
            @SerialName("known_for_department") val knownForDepartment: String? = null,
            @SerialName("profile_path") override val logoSource: String? = null,
            @SerialName("credit_id") val creditId: String? = null,
            @SerialName("department") val department: String? = null,
            @SerialName("job") val job: String? = null,
        ) : HasLogo {
            @Transient
            val isFemale = gender == 1

            @Transient
            val isMale = gender == 2

            @Transient
            val isNonBinary = gender == 3
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
        @SerialName("results") val results: Map<String, Providers> = emptyMap()
    ) {

        fun providerFor(locale: String) = results[locale] ?: results[locale.uppercase()]

        @Serializable
        data class Providers(
            @SerialName("link") val link: String? = null,
            @SerialName("flatrate") val flatrate: Set<Info> = emptySet(),
            @SerialName("buy") val buy: Set<Info> = emptySet(),
            @SerialName("rent") val rent: Set<Info> = emptySet(),
        ) {

            @Serializable
            data class Info(
                @SerialName("logo_path") override val logoSource: String?,
                @SerialName("provider_id") val providerId: Int = 0,
                @SerialName("provider_name") val providerName: String,
                @SerialName("display_priority") val displayPriority: Int = 0
            ) : HasLogo
        }
    }

}
