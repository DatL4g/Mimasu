package dev.datlag.mimasu.module

import androidx.compose.ui.text.intl.Locale
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.serviceLoaderEnabled
import coil3.svg.SvgDecoder
import dev.datlag.mimasu.core.now
import dev.datlag.mimasu.core.toEpochMilliseconds
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.api.DisposableDebounce
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.config.FirebaseRemoteConfigService
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.ui.viewmodel.KodeinViewModelFactory
import dev.datlag.sekret.Secret
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import okio.FileSystem
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

data object NetworkModule {

    const val NAME = "NetworkModule"

    private val _config = MutableStateFlow<Config>(Config.Fetching)
    val config: StateFlow<Config> = _config.asStateFlow()

    private var startedFetching = 0L

    val showSplashscreen: Boolean
        get() {
            return if (config.value is Config.Fetching) {
                startedFetching == 0L || LocalDateTime.now().toEpochMilliseconds() - startedFetching < 3000L
            } else {
                false
            }
        }

    val di: DI.Module = DI.Module(NAME) {
        import(PlatformModule.di)

        bindSingleton<Json> {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
        bindSingleton<ImageLoader> {
            ImageLoader.Builder(instance<PlatformContext>())
                .components {
                    add(
                        KtorNetworkFetcherFactory(
                            httpClient = { instance<HttpClient>() }
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
                .build()
        }
        bindSingleton<TMDB> {
            TMDB.init {
                network {
                    client(instance<HttpClient>())
                }
                apiKey(config.value.getOrThrow().tmdb)
                language(Locale.current.language)
                region(Locale.current.region)
            }
        }
        bindSingleton<KodeinViewModelFactory> {
            KodeinViewModelFactory(this)
        }
        bindSingleton<FirebaseAuthService> {
            FirebaseAuthService()
        }
        bindSingleton<FirebaseAuthDataSource> {
            FirebaseAuthDataSource(firebaseAuthService = instance())
        }
        bindSingleton<DisposableDebounce> {
            DisposableDebounce.create(
                client = instance()
            )
        }
    }

    suspend fun fetchConfig(remoteService: FirebaseRemoteConfigService) {
        if ((config.firstOrNull() ?: config.value) is Config.Failure.Initialize) {
            return
        }
        startedFetching = LocalDateTime.now().toEpochMilliseconds()
        _config.update { Config.Fetching }

        val tmdb = remoteService.getStringResult(Config.TMDB_KEY)
        startedFetching = 0L

        _config.update {
            if (tmdb.isFailure) {
                Config.Failure.Fetching(tmdb.exceptionOrNull())
            } else {
                val tolgee = remoteService.getNullableString(Config.TOLGEE_KEY)?.ifBlank { null }

                Config.Success(
                    tmdb = tmdb.getOrNull() ?: return@update Config.Failure.Fetching(),
                    tolgee = tolgee
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

        fun tolgeeApiKey(): String? = when (this) {
            is Success -> tolgee?.ifBlank { null }
            else -> null
        }

        @Serializable
        data object Fetching : Config

        @Serializable
        sealed interface Failure : Config {

            @Serializable
            data object Initialize : Failure

            @Serializable
            data class Fetching(
                @Transient val throwable: Throwable? = null
            ) : Failure
        }

        @Serializable
        data class Success(
            @Secret val tmdb: String,
            @Secret val tolgee: String?
        ) : Config

        class AccessException(state: Config) : Exception("Tried to access config data while in $state state.")

        companion object {
            const val TMDB_KEY = "tmdb_api_key"
            const val TOLGEE_KEY = "tolgee_api_key"
        }
    }
}