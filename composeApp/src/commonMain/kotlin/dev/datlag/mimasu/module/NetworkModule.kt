package dev.datlag.mimasu.module

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.vanniktech.locale.Locale
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.common.default
import dev.datlag.mimasu.common.localized
import dev.datlag.mimasu.core.common.now
import dev.datlag.mimasu.core.common.toEpochMilliseconds
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.email.FirebaseEmailAuthProvider
import dev.datlag.mimasu.firebase.config.FirebaseRemoteConfigService
import dev.datlag.mimasu.other.Connection
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.sekret.Secret
import dev.datlag.tolgee.I18N
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okio.FileSystem
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.instanceOrNull

data object NetworkModule {

    const val NAME = "NetworkModule"

    /**
     * Remote config key
     */
    private const val TMDB_KEY = "tmdb_api_key"

    /**
     * Remote config key, whether the app is in maintenance mode or not.
     */
    private const val MAINTENANCE_KEY = "maintenance"

    /**
     * Dynamic state of the current fetching status.
     */
    private val _config = MutableStateFlow<Config>(Config.Fetching)
    val config: StateFlow<Config> = _config.asStateFlow()

    /**
     * When [fetchConfig] got called.
     */
    private var startedFetching = 0L

    /**
     * Show splash screen for 3seconds after fetching config, then show dynamic fetchingContent
     */
    val showSplashscreen: Boolean
        get() {
            return if (config.value is Config.Fetching) {
                startedFetching == 0L || LocalDateTime.now().toEpochMilliseconds() - startedFetching < 3000L
            } else {
                false
            }
        }

    const val HTTP_FALLBACK_CLIENT = "HTTP_FALLBACK_CLIENT"

    @OptIn(ExperimentalCoilApi::class)
    val di: DI.Module = DI.Module(NAME) {
        import(PlatformModule.di)

        bindSingleton<Json> {
            Json {
                ignoreUnknownKeys = true
                isLenient = true

                serializersModule = SerializersModule {
                    polymorphic(Trending.Response.Media::class) {
                        subclass(Trending.Response.Media.Movie::class, Trending.Response.Media.Movie.serializer())
                        subclass(Trending.Response.Media.TV::class, Trending.Response.Media.TV.serializer())
                        subclass(Trending.Response.Media.Person::class, Trending.Response.Media.Person.serializer())
                    }
                }
            }
        }
        bindSingleton<ImageLoader> {
            ImageLoader.Builder(instance<PlatformContext>())
                .components {
                    add(
                        KtorNetworkFetcherFactory(
                            httpClient = { instance<HttpClient>() },
                            connectivityChecker = { Connection }
                        )
                    )
                    add(SvgDecoder.Factory())
                }
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(instance<PlatformContext>())
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                        .maxSizeBytes(50L * 1024 * 1024)
                        .build()
                }
                .crossfade(true)
                .extendImageLoader()
                .build()
        }
        bindSingleton<I18N> {
            I18N {
                contentDelivery {
                    id(Sekret.tolgeeContentDelivery(BuildKonfig.packageName)!!)
                }
                client(instance<HttpClient>())
            }
        }
        bindSingleton<TMDB> {
            val locale = instance<I18N>().locale
            val fallback by lazy {
                Locale.default()
            }

            TMDB.create(
                apiKey = config.value.getOrThrow().tmdb,
                client = instance(),
                fallbackClient = instanceOrNull(HTTP_FALLBACK_CLIENT),
                language = locale?.localization ?: fallback.localized(),
                region = locale?.regionCode ?: fallback.territory?.code?.ifBlank { null } ?: fallback.territory?.code3?.ifBlank { null },
            )
        }
        bindSingleton<FirebaseEmailAuthProvider> {
            FirebaseEmailAuthProvider(
                firebaseAuthDataSource = FirebaseAuthDataSource(
                    firebaseAuthService = instance()
                )
            )
        }
    }

    suspend fun fetchConfig(remoteService: FirebaseRemoteConfigService) {
        if ((_config.firstOrNull() ?: _config.value) is Config.Failure.Initialize) {
            return
        }
        startedFetching = LocalDateTime.now().toEpochMilliseconds()
        _config.update { Config.Fetching }

        val maintenance = remoteService.getBoolean(MAINTENANCE_KEY, false)
        if (maintenance) {
            startedFetching = 0L
            _config.update { Config.Maintenance }
            return
        }

        val tmdb = remoteService.getString(TMDB_KEY, "").ifBlank { null }

        startedFetching = 0L
        _config.update {
            if (tmdb.isNullOrBlank()) {
                Config.Failure.Fetching
            } else {
                Config.Success(
                    tmdb = tmdb
                )
            }
        }
    }

    fun initializeFailure() {
        _config.update { Config.Failure.Initialize }
    }

    @Serializable
    sealed interface Config {

        fun getOrThrow(): Success {
            return if (this is Success) {
                this
            } else {
                throw AccessException(this)
            }
        }

        @Serializable
        data object Fetching : Config

        @Serializable
        sealed interface Failure : Config {

            @Serializable
            data object Initialize : Failure

            @Serializable
            data object Fetching : Failure
        }

        @Serializable
        data object Maintenance : Config

        @Serializable
        data class Success(
            @Secret val tmdb: String
        ) : Config

        class AccessException(state: Config) : Exception("Tried to access config data while in $state state.")
    }
}