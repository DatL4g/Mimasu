package dev.datlag.mimasu.common

import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.core.model.UpdateInfo
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.tolgee.Tolgee
import io.ktor.client.HttpClient
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.DirectDI
import org.kodein.di.instanceOrNull

fun DIAware.updateInfo(): UpdateInfo {
    val instance by this.instanceOrNull<UpdateInfo>()
    return instance ?: UpdateInfo.Default
}

fun DirectDI.updateInfo(): UpdateInfo {
    return this.instanceOrNull<UpdateInfo>() ?: UpdateInfo.Default
}

fun DIAware.isTv(): Boolean {
    val instance by this.instanceOrNull<Boolean>("TELEVISION")
    return instance ?: false
}

fun DirectDI.isTv(): Boolean {
    return this.instanceOrNull<Boolean>("TELEVISION") ?: false
}

fun Tolgee.Companion.init(di: DI, config: NetworkModule.Config = NetworkModule.config.value) {
    val httpClient by di.instanceOrNull<HttpClient>()

    init {
        apiKey = config.getTolgeeApiKey()
        network {
            httpClient?.let { client(it) }
        }
        contentDelivery {
            id(Sekret.tolgeeContentDelivery(BuildKonfig.packageName))
        }
    }
}