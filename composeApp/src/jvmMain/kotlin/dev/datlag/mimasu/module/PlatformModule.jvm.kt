package dev.datlag.mimasu.module

import coil3.ImageLoader
import coil3.PlatformContext
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

    private const val NAME = "JVMPlatformModule"

    actual val di: DI.Module = DI.Module(NAME) {
        bindSingleton<PlatformContext> {
            PlatformContext.INSTANCE
        }
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
            PackageResolver()
        }
    }
}

actual fun ImageLoader.Builder.extendImageLoader(): ImageLoader.Builder {
    return this
}