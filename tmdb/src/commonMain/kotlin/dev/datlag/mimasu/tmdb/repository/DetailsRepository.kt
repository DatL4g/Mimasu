package dev.datlag.mimasu.tmdb.repository

import com.mayakapps.kache.InMemoryKache
import dev.datlag.mimasu.core.withNonEmptyContext
import dev.datlag.mimasu.tmdb.api.Details
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.call.body
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

    suspend fun movie(id: Int): Result<Movie?> = withNonEmptyContext(context) {
        suspendCatching {
            movieKache.getOrPut(id) {
                val response = details.movie(
                    apiKey = apiKey,
                    id = id,
                    language = language
                )

                response.body<Movie>()
            }
        }
    }
}