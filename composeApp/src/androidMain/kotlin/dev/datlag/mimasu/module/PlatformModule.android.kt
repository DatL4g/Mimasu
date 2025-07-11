package dev.datlag.mimasu.module

import android.content.Context
import android.os.Build
import androidx.media3.common.util.UnstableApi
import com.appmattus.certificatetransparency.cache.AndroidDiskCache
import com.appmattus.certificatetransparency.certificateTransparencyInterceptor
import com.google.net.cronet.okhttptransport.CronetInterceptor
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.common.firebaseDataSource
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProviderAndroid
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProviderAndroid
import dev.datlag.mimasu.other.AdManager
import dev.datlag.mimasu.ui.Cronet
import dev.datlag.mimasu.ui.GoogleProvider
import dev.datlag.mimasu.ui.common.cronetEngine
import dev.datlag.tooling.Platform
import dev.datlag.tooling.scopeCatching
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import okhttp3.Interceptor
import org.chromium.net.CronetEngine
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

@UnstableApi
actual object PlatformModule {

    private const val NAME = "AndroidPlatformModule"

    private const val TAG_CERT_TRANSPARENT = "CERTIFICATE_TRANSPARENCY"

    actual val di: DI.Module = DI.Module(NAME) {
        import(ExtensionModule.di)
        import(VideoModule.di)

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
        bindSingleton<Interceptor>(TAG_CERT_TRANSPARENT) {
            certificateTransparencyInterceptor {
                diskCache = AndroidDiskCache(instance<Context>())
            }
        }
        bindSingleton<HttpClient> {
            HttpClient(OkHttp) {
                followRedirects = true
                engine {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.BAKLAVA && !Platform.isTelevision(instance<Context>())) {
                        addNetworkInterceptor(instance(TAG_CERT_TRANSPARENT))
                    }
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
        bindProvider<GoogleProvider> {
            // Use provider as initial Sekret call may fail -> bindSingleton always null
            GoogleProvider.basedOn(Sekret.firebaseWebOrAuthId(BuildKonfig.packageName)) { serverClientId ->
                FirebaseGoogleAuthProviderAndroid(
                    firebaseAuthDataSource = firebaseDataSource(),
                    serverClientId = serverClientId
                )
            }
        }
        bindSingleton<FirebaseGitHubAuthProvider> {
            FirebaseGitHubAuthProviderAndroid(firebaseAuthDataSource = firebaseDataSource())
        }
        bindSingleton<AdManager> {
            AdManager(context = instance())
        }
    }
}