package dev.datlag.mimasu.ui.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Composable
expect fun BannerAd(
    type: Banner,
    modifier: Modifier = Modifier
)

@Serializable
sealed interface Banner {

    @Serializable
    data object Home : Banner

    @Serializable
    data object Space : Banner
}