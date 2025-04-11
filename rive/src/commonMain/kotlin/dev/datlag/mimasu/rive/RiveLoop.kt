package dev.datlag.mimasu.rive

import kotlinx.serialization.Serializable

@Serializable
sealed interface RiveLoop {

    @Serializable
    data object OneShot : RiveLoop

    @Serializable
    data object Loop : RiveLoop

    @Serializable
    data object PingPong : RiveLoop

    @Serializable
    data object Auto : RiveLoop
}