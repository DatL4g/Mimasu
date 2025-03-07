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
import dev.datlag.mimasu.tmdb.api.createDetails
import dev.datlag.mimasu.tmdb.api.createDiscover
import dev.datlag.mimasu.tmdb.api.createFind
import dev.datlag.mimasu.tmdb.api.createTrending
import dev.datlag.mimasu.tmdb.api.createTvSeriesList
import dev.datlag.mimasu.tmdb.api.createWatchProviders
import dev.datlag.mimasu.tmdb.model.TrendingWindow
import dev.datlag.mimasu.tmdb.repository.DetailsRepository
import dev.datlag.mimasu.tmdb.repository.PopularRepository
import dev.datlag.mimasu.tmdb.repository.TrendingRepository
import dev.datlag.mimasu.tmdb.repository.WatchProvidersRepository
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlin.jvm.JvmStatic
import kotlin.time.Duration.Companion.days

data class TMDB internal constructor(
    @Secret private val apiKey: String,
    private val certifications: Certifications,
    private val companies: Companies,
    private val credits: Credits,
    private val find: Find,
    val trending: TrendingRepository,
    val popular: PopularRepository,
    val watchProviders: WatchProvidersRepository,
    val details: DetailsRepository
) {

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"
        internal const val ORIGINAL_IMAGE = "https://image.tmdb.org/t/p/original/"
        internal const val W500_IMAGE = "https://image.tmdb.org/t/p/w500/"

        fun create(
            apiKey: String,
            client: HttpClient,
            fallbackClient: HttpClient?,
            language: String,
            region: String?,
            baseUrl: String = BASE_URL
        ): TMDB {
            val ktorfit = ktorfit {
                baseUrl(baseUrl)
                httpClient(client)
            }
            val fallbackKtorfit = fallbackClient?.let {
                ktorfit {
                    baseUrl(baseUrl)
                    httpClient(it)
                }
            }

            return TMDB(
                apiKey = apiKey,
                certifications = ktorfit.createCertifications(),
                companies = ktorfit.createCompanies(),
                credits = ktorfit.createCredits(),
                find = ktorfit.createFind(),
                trending = TrendingRepository(
                    apiKey = apiKey,
                    trending = ktorfit.createTrending(),
                    language = language
                ),
                popular = PopularRepository(
                    apiKey = apiKey,
                    discover = ktorfit.createDiscover(),
                    language = language,
                    region = region
                ),
                watchProviders = WatchProvidersRepository(
                    apiKey = apiKey,
                    watchProviders = ktorfit.createWatchProviders(),
                    language = language
                ),
                details = DetailsRepository(
                    apiKey = apiKey,
                    details = ktorfit.createDetails(),
                    fallbackDetails = fallbackKtorfit?.createDetails(),
                    language = language
                )
            )
        }
    }
}