package dev.datlag.mimasu.firebase.firestore

import dev.datlag.mimasu.core.addSafely
import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class MovieData(
    @SerialName(BOOKMARKED) val bookmarked: Boolean,
    @SerialName(TMDB_ID) val tmdbId: Int,
    @SerialName(IMDB_ID) val imdbId: String? = null,
    @SerialName(WATCH_PROGRESS) val watchProgress: Long = 0L,
    @SerialName(LENGTH) val length: Long = 0L,
    @SerialName(FINISH_THRESHOLD) val finishThreshold: Long = 0L,
    @SerialName(WATCH_LANGUAGE) val watchLanguage: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) @SerialName(LAST_UPDATED) val lastUpdated: BaseTimestamp = Timestamp.ServerTimestamp,
    @SerialName(LAST_WATCHED) val lastWatched: BaseTimestamp? = null,
) {

    internal fun mergeWith(other: MovieData): MovieData {
        return if (tmdbId == other.tmdbId) {
            MovieData(
                bookmarked = bookmarked,
                tmdbId = tmdbId,
                imdbId = imdbId?.ifBlank { null } ?: other.imdbId,
                watchProgress = watchProgress.takeIf { it > 0L } ?: other.watchProgress,
                length = length.takeIf { it >= 0 } ?: other.length,
                finishThreshold = finishThreshold.takeIf { it >= 0 } ?: other.finishThreshold,
                watchLanguage = watchLanguage?.ifBlank { null } ?: other.watchLanguage,
                lastUpdated = lastUpdated,
                lastWatched = lastWatched
            )
        } else {
            this
        }
    }

    internal fun mergeWithCollection(collection: Collection<MovieData>): Collection<MovieData> {
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

    internal companion object {
        const val COLLECTION = "movie"
        const val GROUP = "items"

        const val BOOKMARKED = "bookmarked"
        const val TMDB_ID = "tmdbId"
        const val IMDB_ID = "imdbId"
        const val WATCH_PROGRESS = "watchProgress"
        const val LENGTH = "length"
        const val FINISH_THRESHOLD = "finishThreshold"
        const val WATCH_LANGUAGE = "watchLanguage"
        const val LAST_UPDATED = "lastUpdated"
        const val LAST_WATCHED = "lastWatched"
    }
}
