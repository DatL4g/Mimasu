package dev.datlag.mimasu.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class RootConfig {

    @Serializable
    data object Initial : RootConfig()

    @Serializable
    data object Login : RootConfig()

    @Serializable
    data object Video : RootConfig()
}