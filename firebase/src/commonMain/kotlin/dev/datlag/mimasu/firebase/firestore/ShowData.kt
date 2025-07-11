package dev.datlag.mimasu.firebase.firestore

import co.touchlab.kermit.Logger
import dev.datlag.mimasu.core.addSafely
import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.max

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ShowData(
    @SerialName(BOOKMARKED) private val _bookmarked: Boolean? = null,
    @SerialName(TMDB_ID) val tmdbId: Int,
    @SerialName(IMDB_ID) val imdbId: String? = null,
    @SerialName(SEASON) val season: Int? = null,
    @SerialName(NUMBER_OF_SEASONS) val numberOfSeasons: Int? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) @SerialName(LAST_UPDATED) val lastUpdated: BaseTimestamp = Timestamp.ServerTimestamp,
) {

    @Transient
    val bookmarked = _bookmarked ?: false

    internal fun mergeWith(other: ShowData): ShowData {
        return if (tmdbId == other.tmdbId) {
            ShowData(
                _bookmarked = bookmarked,
                tmdbId = tmdbId,
                imdbId = imdbId?.ifBlank { null } ?: other.imdbId,
                season = season?.takeIf { it >= 0 } ?: other.season,
                numberOfSeasons = numberOfSeasons?.takeIf { it > 0 } ?: other.numberOfSeasons,
                lastUpdated = lastUpdated
            )
        } else {
            this
        }
    }

    internal fun mergeWithCollection(collection: Collection<ShowData>): Collection<ShowData> {
        val defaultValue = collection.firstOrNull { it.tmdbId == tmdbId }
        val merged = defaultValue?.let(::mergeWith) ?: this

        return collection.toMutableList().apply {
            if (defaultValue != null) {
                val index = indexOf(defaultValue).takeIf { it >= 0 }

                if (index != null) {
                    set(index, merged)
                } else {
                    addSafely(0, merged)
                }
            } else {
                addSafely(0, merged)
            }
        }.distinctBy { it.tmdbId }
    }

    @Serializable
    data class EpisodeData(
        @SerialName(NUMBER) val number: Int,
        @SerialName(ID) val id: String? = null,
        @SerialName(MARKED_AS_WATCHED) val markedAsWatched: Boolean? = null,
        @SerialName(FINISHED) val finished: Boolean? = null
    ) {

        internal fun mergeWith(other: EpisodeData): EpisodeData {
            return if (number == other.number) {
                EpisodeData(
                    number = number,
                    id = id?.ifBlank { null } ?: other.id,
                    markedAsWatched = markedAsWatched,
                    finished = finished
                )
            } else {
                this
            }
        }

        internal fun mergeWithCollection(collection: Collection<EpisodeData>): Collection<EpisodeData> {
            val defaultValue = collection.firstOrNull { it.number == number }
            val merged = defaultValue?.let(::mergeWith) ?: this

            return collection.toMutableList().apply {
                if (defaultValue != null) {
                    val index = indexOf(defaultValue).takeIf { it >= 0 }

                    if (index != null) {
                        set(index, merged)
                    } else {
                        addSafely(max(number - 1, 0), merged)
                    }
                } else {
                    addSafely(max(number - 1, 0), merged)
                }
            }.distinctBy { it.number }
        }

        internal companion object {
            private const val COLLECTION_PREFIX = "season"
            private const val DOCUMENT_PREFIX = "episode"

            const val NUMBER = "number"
            const val ID = "id"
            const val MARKED_AS_WATCHED = "markedAsWatched"
            const val FINISHED = "finished"

            fun collectionForSeason(number: Int) = "$COLLECTION_PREFIX$number"
            fun documentForNumber(number: Int) = "$DOCUMENT_PREFIX$number"
        }
    }

    internal companion object {
        const val COLLECTION = "show"
        const val GROUP = "items"

        const val BOOKMARKED = "bookmarked"
        const val TMDB_ID = "tmdbId"
        const val IMDB_ID = "imdbId"
        const val SEASON = "season"
        const val NUMBER_OF_SEASONS = "numberOfSeasons"
        const val LAST_UPDATED = "lastUpdated"
    }
}
