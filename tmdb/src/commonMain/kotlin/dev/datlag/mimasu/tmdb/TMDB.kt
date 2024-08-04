package dev.datlag.mimasu.tmdb

import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.mimasu.tmdb.api.Certifications
import dev.datlag.mimasu.tmdb.api.Companies
import dev.datlag.mimasu.tmdb.api.Credits
import dev.datlag.mimasu.tmdb.api.Discover
import dev.datlag.mimasu.tmdb.api.Find
import dev.datlag.mimasu.tmdb.api.Trending as TrendingAPI
import dev.datlag.mimasu.tmdb.api.TvSeriesList
import dev.datlag.mimasu.tmdb.api.createCertifications
import dev.datlag.mimasu.tmdb.api.createCompanies
import dev.datlag.mimasu.tmdb.api.createCredits
import dev.datlag.mimasu.tmdb.api.createDiscover
import dev.datlag.mimasu.tmdb.api.createFind
import dev.datlag.mimasu.tmdb.api.createTrending
import dev.datlag.mimasu.tmdb.api.createTvSeriesList
import dev.datlag.mimasu.tmdb.model.TrendingWindow
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlin.time.Duration.Companion.days

data class TMDB internal constructor(
    @Secret private val apiKey: String,
    private val certifications: Certifications,
    private val companies: Companies,
    private val credits: Credits,
    private val discover: Discover,
    private val find: Find,
    val trending: Trending,
    private val tvSeriesList: TvSeriesList
) {

    data class Trending internal constructor(
        @Secret private val apiKey: String,
        private val trending: TrendingAPI
    ) {
        private val allKache = InMemoryKache<TrendingWindow, TrendingAPI.Response>(maxSize = 5 * 1024 * 1024) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = 1.days
        }

        private val movieKache = InMemoryKache<TrendingWindow, TrendingAPI.Response>(maxSize = 5 * 1024 * 1024) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = 1.days
        }

        private val tvKache = InMemoryKache<TrendingWindow, TrendingAPI.Response>(maxSize = 5 * 1024 * 1024) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = 1.days
        }

        private val personKache = InMemoryKache<TrendingWindow, TrendingAPI.Response>(maxSize = 5 * 1024 * 1024) {
            strategy = KacheStrategy.LRU
            expireAfterWriteDuration = 1.days
        }

        suspend fun all(
            window: TrendingWindow,
            language: String
        ) = allKache.getOrPut(window) {
            val response = trending.all(
                apiKey = apiKey,
                window = window.value,
                language = language
            )

            if (response.status.isSuccess()) {
                suspendCatching {
                    response.body<TrendingAPI.Response>()
                }.onFailure {
                    Napier.e("Failed parsing", it)
                }.getOrNull()
            } else {
                Napier.e("Failed request: [${response.status.value}] ${response.status.description}")
                null
            }
        }

        suspend fun movie(
            window: TrendingWindow,
            language: String
        ) = movieKache.getOrPut(window) {
            val response = trending.movies(
                apiKey = apiKey,
                window = window.value,
                language = language
            )

            if (response.status.isSuccess()) {
                suspendCatching {
                    response.body<TrendingAPI.Response>()
                }.onFailure {
                    Napier.e("Failed parsing", it)
                }.getOrNull()
            } else {
                Napier.e("Failed request: [${response.status.value}] ${response.status.description}")
                null
            }
        }

        suspend fun tv(
            window: TrendingWindow,
            language: String
        ) = tvKache.getOrPut(window) {
            val response = trending.tv(
                apiKey = apiKey,
                window = window.value,
                language = language
            )

            if (response.status.isSuccess()) {
                suspendCatching {
                    response.body<TrendingAPI.Response>()
                }.onFailure {
                    Napier.e("Failed parsing", it)
                }.getOrNull()
            } else {
                Napier.e("Failed request: [${response.status.value}] ${response.status.description}")
                null
            }
        }

        suspend fun person(
            window: TrendingWindow,
            language: String
        ) = personKache.getOrPut(window) {
            val response = trending.people(
                apiKey = apiKey,
                window = window.value,
                language = language
            )

            if (response.status.isSuccess()) {
                suspendCatching {
                    response.body<TrendingAPI.Response>()
                }.onFailure {
                    Napier.e("Failed parsing", it)
                }.getOrNull()
            } else {
                Napier.e("Failed request: [${response.status.value}] ${response.status.description}")
                null
            }
        }
    }

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun create(
            apiKey: String,
            client: HttpClient,
            baseUrl: String = BASE_URL
        ): TMDB {
            val ktorfit = ktorfit {
                baseUrl(baseUrl)
                httpClient(client)
            }

            return TMDB(
                apiKey = apiKey,
                certifications = ktorfit.createCertifications(),
                companies = ktorfit.createCompanies(),
                credits = ktorfit.createCredits(),
                discover = ktorfit.createDiscover(),
                find = ktorfit.createFind(),
                trending = Trending(
                    apiKey = apiKey,
                    trending = ktorfit.createTrending()
                ),
                tvSeriesList = ktorfit.createTvSeriesList()
            )
        }
    }
}