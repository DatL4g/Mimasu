package dev.datlag.mimasu.firebase.firestore

import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ShowData(
    @SerialName(BOOKMARKED) val bookmarked: Boolean = false,
    @SerialName(TMDB_ID) val tmdbId: Int,
    @SerialName(IMDB_ID) val imdbId: String? = null,
    @SerialName(SEASON) val season: Int? = null,
    @SerialName(NUMBER_OF_SEASONS) val numberOfSeasons: Int? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) @SerialName(LAST_UPDATED) val lastUpdated: BaseTimestamp = Timestamp.ServerTimestamp,
) {

    internal fun mergeWith(other: ShowData): ShowData {
        return if (tmdbId == other.tmdbId) {
            ShowData(
                bookmarked = bookmarked,
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
                    add(0, merged)
                }
            } else {
                add(0, merged)
            }
        }.distinctBy { it.tmdbId }
    }

    @Serializable
    data class EpisodeData(
        @SerialName(MARKED_AS_WATCHED) val markedAsWatched: Boolean = false,
        @SerialName(FINISHED) val finished: Boolean = false
    ) {

        internal companion object {
            private const val COLLECTION_PREFIX = "season"

            const val MARKED_AS_WATCHED = "markedAsWatched"
            const val FINISHED = "finished"

            fun collectionForSeason(number: Int) = "$COLLECTION_PREFIX$number"
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
