package dev.datlag.mimasu.ui

import org.chromium.net.CronetEngine

sealed interface Cronet {
    val engine: CronetEngine?
        get() = null

    data class Available(override val engine: CronetEngine) : Cronet
    data object NonAvailable : Cronet
}