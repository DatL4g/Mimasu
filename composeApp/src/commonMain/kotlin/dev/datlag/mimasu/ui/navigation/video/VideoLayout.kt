package dev.datlag.mimasu.ui.navigation.video

import kotlinx.serialization.Serializable

@Serializable
sealed interface VideoLayout {

    val isPortraitOrUnknown: Boolean
        get() = this is Portrait || this is Unknown

    val isLandscapeOrUnknown: Boolean
        get() = this is Landscape || this is Unknown

    @Serializable
    data object Unknown : VideoLayout

    @Serializable
    data object Portrait : VideoLayout

    @Serializable
    data object Landscape : VideoLayout

    companion object
}