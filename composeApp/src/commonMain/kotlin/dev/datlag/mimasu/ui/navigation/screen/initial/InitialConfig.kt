package dev.datlag.mimasu.ui.navigation.screen.initial

import kotlinx.serialization.Serializable

@Serializable
sealed class InitialConfig {

    @Serializable
    data object Home : InitialConfig()
}