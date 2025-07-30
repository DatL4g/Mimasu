package dev.datlag.mimasu.module

import androidx.compose.ui.text.intl.Locale
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.api.DisposableDebounce
import dev.datlag.mimasu.firebase.auth.api.GoogleDoH
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.ui.other.Network
import dev.datlag.mimasu.ui.viewmodel.KodeinViewModelFactory
import dev.datlag.tooling.compose.TargetIO
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okio.FileSystem
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

data object NetworkModule {

    const val NAME = "NetworkModule"

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
                    context(Dispatchers.Virtual ?: Dispatchers.TargetIO)
                }
                apiKey(Network.tmdbApiKey)
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
        bindSingleton<GoogleDoH> {
            GoogleDoH.create(
                client = instance()
            )
        }
    }
}