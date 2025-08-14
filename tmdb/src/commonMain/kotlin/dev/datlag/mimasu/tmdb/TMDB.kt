package dev.datlag.mimasu.tmdb

import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.mimasu.tmdb.api.createDetails
import dev.datlag.mimasu.tmdb.api.createDiscover
import dev.datlag.mimasu.tmdb.api.createMovieLists
import dev.datlag.mimasu.tmdb.api.createSearch
import dev.datlag.mimasu.tmdb.api.createTrending
import dev.datlag.mimasu.tmdb.api.createTvSeriesLists
import dev.datlag.mimasu.tmdb.converter.BoolStringConverter
import dev.datlag.mimasu.tmdb.model.trending.TimeWindow
import dev.datlag.mimasu.tmdb.repository.DetailsRepository
import dev.datlag.mimasu.tmdb.repository.DiscoverRepository
import dev.datlag.mimasu.tmdb.repository.MovieListsRepository
import dev.datlag.mimasu.tmdb.repository.SearchRepository
import dev.datlag.mimasu.tmdb.repository.TrendingRepository
import dev.datlag.mimasu.tmdb.repository.TvSeriesListsRepository
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import kotlin.coroutines.CoroutineContext

@ConsistentCopyVisibility
data class TMDB internal constructor(
    val network: Network,
    val trending: TrendingRepository,
    val search: SearchRepository,
    val movieLists: MovieListsRepository,
    val tvSeriesLists: TvSeriesListsRepository,
    val details: DetailsRepository,
    val discoverRepository: DiscoverRepository
) {

    class Builder {

        private lateinit var apiKey: String

        var apiUrl: String = BASE_URL
            set(value) {
                field = value.trim().ifBlank { null } ?: BASE_URL
            }

        lateinit var network: Network

        lateinit var language: String
        var region: String? = null

        fun apiKey(key: String) = apply {
            this.apiKey = key
        }

        fun apiUrl(url: String) = apply {
            this.apiUrl = url
        }

        fun network(network: Network) = apply {
            this.network = network
        }

        fun network(builder: Network.Builder.() -> Unit) = apply {
            this.network = Network.Builder().apply(builder).build()
        }

        fun language(locale: String) = apply {
            this.language = locale
        }

        fun region(locale: String) = apply {
            this.region = locale
        }

        fun build(): TMDB {
            val ktorfit = ktorfit {
                baseUrl(apiUrl)
                httpClient(network.client)
                converterFactories(TimeWindow.Converter, BoolStringConverter)
            }

            return TMDB(
                network = network,
                trending = TrendingRepository(
                    apiKey = apiKey,
                    trending = ktorfit.createTrending(),
                    language = language,
                    context = network.context
                ),
                search = SearchRepository(
                    apiKey = apiKey,
                    search = ktorfit.createSearch(),
                    language = language,
                    context = network.context
                ),
                movieLists = MovieListsRepository(
                    apiKey = apiKey,
                    api = ktorfit.createMovieLists(),
                    language = language,
                    region = region,
                    context = network.context
                ),
                tvSeriesLists = TvSeriesListsRepository(
                    apiKey = apiKey,
                    api = ktorfit.createTvSeriesLists(),
                    language = language,
                    context = network.context
                ),
                details = DetailsRepository(
                    apiKey = apiKey,
                    details = ktorfit.createDetails(),
                    language = language,
                    context = network.context
                ),
                discoverRepository = DiscoverRepository(
                    apiKey = apiKey,
                    discover = ktorfit.createDiscover(),
                    language = language,
                    context = network.context
                )
            )
        }

    }

    @ConsistentCopyVisibility
    data class Network internal constructor(
        val client: HttpClient,
        val fallbackClient: HttpClient?,
        val context: CoroutineContext
    ) {

        class Builder {
            lateinit var client: HttpClient
            var fallbackClient: HttpClient? = null
            lateinit var context: CoroutineContext

            fun client(client: HttpClient) = apply {
                this.client = client
            }

            fun client(engine: HttpClientEngine) = client(HttpClient(engine))

            fun <T : HttpClientEngineConfig> client(engineFactory: HttpClientEngineFactory<T>) = client(HttpClient(engineFactory))

            fun client(config: HttpClientConfig<*>.() -> Unit) = client(HttpClient(config))

            fun client(
                engine: HttpClientEngine,
                config: HttpClientConfig<*>.() -> Unit
            ) = client(HttpClient(engine, config))

            fun <T : HttpClientEngineConfig> client(
                engineFactory: HttpClientEngineFactory<T>,
                config: HttpClientConfig<T>.() -> Unit
            ) = client(HttpClient(engineFactory, config))

            fun fallbackClient(client: HttpClient) = apply {
                this.fallbackClient = client
            }

            fun fallbackClient(engine: HttpClientEngine) = fallbackClient(HttpClient(engine))

            fun <T : HttpClientEngineConfig> fallbackClient(engineFactory: HttpClientEngineFactory<T>) = fallbackClient(HttpClient(engineFactory))

            fun fallbackClient(config: HttpClientConfig<*>.() -> Unit) = fallbackClient(HttpClient(config))

            fun fallbackClient(
                engine: HttpClientEngine,
                config: HttpClientConfig<*>.() -> Unit
            ) = fallbackClient(HttpClient(engine, config))

            fun <T : HttpClientEngineConfig> fallbackClient(
                engineFactory: HttpClientEngineFactory<T>,
                config: HttpClientConfig<T>.() -> Unit
            ) = fallbackClient(HttpClient(engineFactory, config))

            fun context(context: CoroutineContext) = apply {
                this.context = context
            }

            fun build(): Network = Network(
                client = client,
                fallbackClient = fallbackClient,
                context = context
            )
        }
    }

    companion object {
        internal const val BASE_URL = "https://api.themoviedb.org/3/"
        internal const val ORIGINAL_IMAGE = "https://image.tmdb.org/t/p/original/"
        internal const val W500_IMAGE = "https://image.tmdb.org/t/p/w500/"
        internal const val W400_IMAGE = "https://image.tmdb.org/t/p/w400/"
        internal const val W300_IMAGE = "https://image.tmdb.org/t/p/w300/"
        internal const val W200_IMAGE = "https://image.tmdb.org/t/p/w200/"

        fun init(builder: Builder.() -> Unit): TMDB {
            return TMDB.Builder().apply(builder).build()
        }
    }
}