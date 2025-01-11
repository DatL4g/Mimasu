package dev.datlag.mimasu.module

import android.content.Context
import androidx.car.app.connection.CarConnection
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import coil3.ImageLoader
import coil3.request.allowHardware
import com.google.net.cronet.okhttptransport.CronetInterceptor
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.common.cronetEngine
import dev.datlag.mimasu.core.MimasuConnection
import dev.datlag.mimasu.core.model.UpdateInfo
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProviderAndroid
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProviderAndroid
import dev.datlag.mimasu.module.NetworkModule.HTTP_FALLBACK_CLIENT
import dev.datlag.mimasu.other.PackageResolver
import dev.datlag.mimasu.ui.navigation.screen.video.PlayerWrapper
import dev.datlag.mimasu.ui.navigation.screen.video.VideoController
import dev.datlag.mimasu.ui.navigation.screen.video.VideoPlayerState
import dev.datlag.tooling.Platform
import dev.datlag.tooling.scopeCatching
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import okio.FileSystem
import org.chromium.net.CronetEngine
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import java.net.InetAddress

@UnstableApi
actual data object PlatformModule {

    private const val NAME = "AndroidPlatformModule"
    private const val DNS_BASE_CLIENT = "DNS_BASE_CLIENT"
    private const val DNS_URL = "https://dns.google/dns-query"
    private const val DNS_HOST_1 = "8.8.8.8"
    private const val DNS_HOST_2 = "8.8.4.4"

    actual val di: DI.Module = DI.Module(NAME) {
        bindSingleton<Cronet> {
            scopeCatching {
                CronetEngine.Builder(instance<Context>())
                    .enableBrotli(true)
                    .enableQuic(true)
                    .enableHttp2(true)
                    .enablePublicKeyPinningBypassForLocalTrustAnchors(true)
                    .build()
            }.getOrNull()?.let(Cronet::Available) ?: Cronet.NonAvailable
        }
        bindSingleton<HttpClient> {
            HttpClient(OkHttp) {
                followRedirects = true
                engine {
                    // Add the Cronet interceptor last, otherwise the subsequent interceptors will be skipped.
                    cronetEngine()?.let {
                        addInterceptor(
                            CronetInterceptor.newBuilder(it).build()
                        )
                    }
                }
                install(ContentNegotiation) {
                    json(instance(), ContentType.Application.Json)
                    json(instance(), ContentType.Text.Plain)
                }
                install(HttpCache)
            }
        }
        bindSingleton<OkHttpClient>(DNS_BASE_CLIENT) {
            OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build()
        }
        bindSingleton<Dns> {
            DnsOverHttps.Builder()
                .client(instance(DNS_BASE_CLIENT))
                .url(DNS_URL.toHttpUrl())
                .bootstrapDnsHosts(
                    InetAddress.getByName(DNS_HOST_1),
                    InetAddress.getByName(DNS_HOST_2)
                )
                .build()
        }
        bindSingleton<HttpClient>(HTTP_FALLBACK_CLIENT) {
            HttpClient(OkHttp) {
                followRedirects = true
                engine {
                    config {
                        dns(instance())
                    }
                }
                install(ContentNegotiation) {
                    json(instance(), ContentType.Application.Json)
                    json(instance(), ContentType.Text.Plain)
                }
                install(HttpCache)
            }
        }
        bindSingleton<PackageResolver> {
            PackageResolver(instance<Context>())
        }
        bindSingleton<FirebaseAuthService> {
            FirebaseAuthService()
        }
        bindSingleton<FirebaseGoogleAuthProvider> {
            FirebaseGoogleAuthProviderAndroid(
                firebaseAuthDataSource = FirebaseAuthDataSource(
                    firebaseAuthService = instance()
                ),
                serverClientId = Sekret.firebaseGoogleId(BuildKonfig.packageName)!!,
                context = instance()
            )
        }
        bindSingleton<FirebaseGitHubAuthProvider> {
            FirebaseGitHubAuthProviderAndroid(
                firebaseAuthDataSource = FirebaseAuthDataSource(
                    firebaseAuthService = instance()
                )
            )
        }
        bindSingleton<DatabaseProvider> {
            StandaloneDatabaseProvider(instance())
        }
        bindSingleton<Cache> {
            SimpleCache(
                (FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "video").toFile(),
                LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024),
                instance()
            )
        }
        bindSingleton<UpdateInfo> {
            MimasuConnection.Update
        }
        bindSingleton<Boolean>("TELEVISION") {
            Platform.isTelevision(instance<Context>())
        }
        bindProvider {
            PlayerWrapper(
                context = instance(),
                cronetEngine = cronetEngine(),
                cache = instance()
            )
        }
        bindProvider<VideoController> {
            VideoPlayerState(player = instance())
        }
        bindSingleton<CarConnection> {
            CarConnection(instance())
        }
    }

    sealed interface Cronet {
        val engine: CronetEngine?
            get() = null

        data class Available(override val engine: CronetEngine) : Cronet
        data object NonAvailable : Cronet
    }
}

actual fun ImageLoader.Builder.extendImageLoader(): ImageLoader.Builder {
    return this.allowHardware(false)
}