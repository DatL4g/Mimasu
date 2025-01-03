package dev.datlag.mimasu.common

import dev.datlag.mimasu.core.model.UpdateInfo
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