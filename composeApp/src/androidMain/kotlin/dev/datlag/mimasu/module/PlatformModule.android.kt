package dev.datlag.mimasu.module

import android.content.Context
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
import dev.datlag.mimasu.module.PlatformModule.Cronet.Available
import dev.datlag.mimasu.other.PackageResolver
import dev.datlag.mimasu.ui.navigation.screen.video.PlayerWrapper
import dev.datlag.mimasu.ui.navigation.screen.video.VideoController
import dev.datlag.mimasu.ui.navigation.screen.video.VideoPlayerState
import dev.datlag.tooling.Platform
import dev.datlag.tooling.scopeCatching
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import okio.FileSystem
import org.chromium.net.CronetEngine
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

@UnstableApi
actual data object PlatformModule {

    private const val NAME = "AndroidPlatformModule"

    actual val di: DI.Module = DI.Module(NAME) {
        bindSingleton<Cronet> {
            scopeCatching {
                CronetEngine.Builder(instance<Context>())
                    .enableBrotli(true)
                    .enableQuic(true)
                    .enableHttp2(true)
                    .enablePublicKeyPinningBypassForLocalTrustAnchors(true)
                    .build()
            }.getOrNull()?.let(::Available) ?: Cronet.NonAvailable
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