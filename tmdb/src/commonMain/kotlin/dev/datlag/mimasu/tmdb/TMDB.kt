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
import dev.datlag.mimasu.tmdb.repository.TrendingRepository
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
    val trending: TrendingRepository,
    private val tvSeriesList: TvSeriesList
) {

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun create(
            apiKey: String,
            client: HttpClient,
            language: String,
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
                trending = TrendingRepository(
                    apiKey = apiKey,
                    trending = ktorfit.createTrending(),
                    language = language
                ),
                tvSeriesList = ktorfit.createTvSeriesList()
            )
        }
    }
}