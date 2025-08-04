package dev.datlag.mimasu.module

import coil3.PlatformContext
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.common.firebaseDataSource
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProviderJS
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

actual object PlatformModule {

    private const val NAME = "JsPlatformModule"

    actual val di: DI.Module = DI.Module(NAME) {
        bindSingleton<PlatformContext> {
            PlatformContext.INSTANCE
        }
        bindSingleton<HttpClient> {
            HttpClient(Js) {
                followRedirects = true

                install(ContentNegotiation) {
                    json(instance(), ContentType.Application.Json)
                    json(instance(), ContentType.Text.Plain)
                }
                install(HttpCache)
            }
        }
        bindSingleton<FirebaseGoogleAuthProvider> {
            FirebaseGoogleAuthProviderJS(
                firebaseAuthDataSource = firebaseDataSource(),
                serverClientId = Sekret.firebaseWebOrAuthId(BuildKonfig.packageName).orEmpty()
            )
        }
    }
}