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
