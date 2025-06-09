package dev.datlag.mimasu.ui.ads

import androidx.compose.runtime.Composable

expect class RewardAdManager {

    fun showRewardAd(
        onRewarded: () -> Unit
    )
}

@Composable
expect fun rememberAdManager(): RewardAdManager