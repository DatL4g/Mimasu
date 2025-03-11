package dev.datlag.mimasu.tmdb

import dev.datlag.sekret.Secret
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

@ConsistentCopyVisibility
data class TMDB internal constructor(
    @Secret private val apiKey: String,
    val apiUrl: String,
    val network: Network
) {

    class Builder {

        private lateinit var apiKey: String

        var apiUrl: String = BASE_URL
            set(value) {
                field = value.trim().ifBlank { null } ?: BASE_URL
            }

        lateinit var network: Network

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

        fun build(): TMDB = TMDB(
            apiKey = apiKey,
            apiUrl = apiUrl,
            network = network
        )

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
            var context: CoroutineContext = Dispatchers.IO

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
        private const val BASE_URL = "https://api.themoviedb.org/3/"
        internal const val ORIGINAL_IMAGE = "https://image.tmdb.org/t/p/original/"
        internal const val W500_IMAGE = "https://image.tmdb.org/t/p/w500/"
    }
}