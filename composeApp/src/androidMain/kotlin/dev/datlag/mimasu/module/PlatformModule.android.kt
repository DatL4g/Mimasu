package dev.datlag.mimasu.module

import android.content.Context
import coil3.ImageLoader
import coil3.request.allowHardware
import dev.datlag.mimasu.other.PackageResolver
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

actual data object PlatformModule {

    private const val NAME = "AndroidPlatformModule"

    actual val di: DI.Module = DI.Module(NAME) {
        bindSingleton<HttpClient> {
            HttpClient(OkHttp) {
                followRedirects = true
                install(ContentNegotiation) {
                    json(instance(), ContentType.Application.Json)
                    json(instance(), ContentType.Text.Plain)
                }
            }
        }
        bindSingleton<PackageResolver> {
            PackageResolver(instance<Context>())
        }
    }
}

actual fun ImageLoader.Builder.extendImageLoader(): ImageLoader.Builder {
    return this.allowHardware(false)
}