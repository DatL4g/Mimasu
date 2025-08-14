package dev.datlag.mimasu.tmdb.model

import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.tooling.scopeCatching
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.LocalDate
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@ConsistentCopyVisibility
@OptIn(ExperimentalSerializationApi::class)
data class Movie internal constructor(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("backdrop_path") override val backdropSource: String? = null,
    @SerialName("id") override val id: Int,
    @SerialName("title") val title: String,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") override val posterSource: String? = null,
    @SerialName("media_type") @EncodeDefault(EncodeDefault.Mode.ALWAYS) override val mediaType: String? = "movie",
    @SerialName("genre_ids") val genreIds: SerializableImmutableSet<Int> = persistentSetOf(),
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("video") val video: Boolean = true,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("vote_count") val voteCount: Int = 0
) : Response, HasBackdrop, HasPoster, HasKana {

    @Transient
    override val kanaSource: String? = title.ifBlank { null }

    @Transient
    override val kanaBackupSource: String? = originalTitle?.ifBlank { null }

    @Transient
    val releaseLocalDate = releaseDate?.ifBlank { null }?.let { scopeCatching {
        LocalDate.parse(it)
    }.getOrNull() }

    @Transient
    val releaseYear = releaseLocalDate?.year
}
