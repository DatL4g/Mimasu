package dev.datlag.mimasu.ui.ads

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
expect class RewardAdManager {

    fun showRewardAd(
        onRewarded: () -> Unit
    )
}

@Composable
expect fun rememberAdManager(): RewardAdManager