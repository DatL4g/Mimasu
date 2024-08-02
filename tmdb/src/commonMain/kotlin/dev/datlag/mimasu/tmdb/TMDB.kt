package dev.datlag.mimasu.tmdb

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
import io.ktor.client.HttpClient

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
        suspend fun all(
            window: TrendingWindow,
            language: String
        ) = trending.all(
            apiKey = apiKey,
            window = window.value,
            language = language
        )
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