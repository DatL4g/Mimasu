package dev.datlag.mimasu.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class RootDialogConfig {

    @Serializable
    data object Video : RootDialogConfig()
}