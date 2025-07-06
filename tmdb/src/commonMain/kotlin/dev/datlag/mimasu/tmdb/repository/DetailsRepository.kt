package dev.datlag.mimasu.tmdb.repository

import com.mayakapps.kache.InMemoryKache
import dev.datlag.mimasu.core.withNonEmptyContext
import dev.datlag.mimasu.kache.async
import dev.datlag.mimasu.tmdb.api.Details
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.details.Person
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.call.body
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.days

class DetailsRepository(
    @Secret private val apiKey: String,
    private val details: Details,
    private val language: String,
    private val context: CoroutineContext
) {

    private val movieKache = InMemoryKache<Int, Movie>(
        maxSize = 5 * 1024 * 1024
    ) {
        expireAfterWriteDuration = 1.days
    }

    private val personKache = InMemoryKache<Int, Person>(
        maxSize = 5 * 1024 * 1024
    ) {
        expireAfterWriteDuration = 1.days
    }

    private val showKache = InMemoryKache<Int, Show>(
        maxSize = 5 * 1024 * 1024
    ) {
        expireAfterWriteDuration = 1.days
    }

    private val showSeasonKache = InMemoryKache<ShowSeasonCacheKey, Season>(
        maxSize = 5 * 1024 * 1024
    ) {
        expireAfterWriteDuration = 1.days
    }

    suspend fun movie(id: Int): Result<Movie?> = withNonEmptyContext(context) {
        suspendCatching {
            movieKache.async(id) {
                val response = details.movie(
                    apiKey = apiKey,
                    id = id,
                    language = language,
                    appendToResponse = listOf(
                        APPEND_CREDITS,
                        APPEND_EXTERNAL_IDS,
                        VIDEOS,
                        WATCH_PROVIDERS
                    ).joinToString(separator = ",")
                )

                response.body<Movie>()
            }
        }
    }

    suspend fun person(id: Int): Result<Person?> = withNonEmptyContext(context) {
        suspendCatching {
            personKache.async(id) {
                val response = details.person(
                    apiKey = apiKey,
                    id = id,
                    language = language
                )

                response.body<Person>()
            }
        }
    }

    suspend fun show(id: Int): Result<Show?> = withNonEmptyContext(context) {
        suspendCatching {
            showKache.async(id) {
                val response = details.show(
                    apiKey = apiKey,
                    id = id,
                    language = language,
                    appendToResponse = listOf(
                        APPEND_CREDITS,
                        APPEND_EXTERNAL_IDS,
                        WATCH_PROVIDERS
                    ).joinToString(separator = ",")
                )

                response.body<Show>()
            }
        }
    }

    suspend fun showSeason(showId: Int, seasonId: Int): Result<Season?> = withNonEmptyContext(context) {
        suspendCatching {
            showSeasonKache.async(
                ShowSeasonCacheKey(
                    showId = showId,
                    seasonId = seasonId
                )
            ) {
                val response = details.showSeason(
                    apiKey = apiKey,
                    showId = showId,
                    seasonId = seasonId,
                    language = language,
                    appendToResponse = listOf(
                        WATCH_PROVIDERS
                    ).joinToString(separator = ",")
                )

                response.body<Season>()
            }
        }
    }

    @Serializable
    private data class ShowSeasonCacheKey(
        val showId: Int,
        val seasonId: Int
    )

    companion object {
        private const val APPEND_CREDITS = "credits"
        private const val APPEND_EXTERNAL_IDS = "external_ids"
        private const val WATCH_PROVIDERS = "watch/providers"
        private const val VIDEOS = "videos"
    }
}