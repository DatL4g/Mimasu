package dev.datlag.mimasu.module

import android.content.Context
import coil3.ImageLoader
import coil3.request.allowHardware
import com.google.net.cronet.okhttptransport.CronetInterceptor
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProviderAndroid
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProviderAndroid
import dev.datlag.mimasu.firebase.config.FirebaseRemoteConfigService
import dev.datlag.mimasu.other.PackageResolver
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import org.chromium.net.CronetEngine
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

actual data object PlatformModule {

    private const val NAME = "AndroidPlatformModule"

    actual val di: DI.Module = DI.Module(NAME) {
        bindSingleton<CronetEngine> {
            CronetEngine.Builder(instance<Context>())
                .enableBrotli(true)
                .enableQuic(true)
                .enableHttp2(true)
                .enablePublicKeyPinningBypassForLocalTrustAnchors(true)
                .build()
        }
        bindSingleton<HttpClient> {
            HttpClient(OkHttp) {
                followRedirects = true
                engine {
                    addInterceptor(
                        CronetInterceptor.newBuilder(instance()).build()
                    )
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
    }
}

actual fun ImageLoader.Builder.extendImageLoader(): ImageLoader.Builder {
    return this.allowHardware(false)
}