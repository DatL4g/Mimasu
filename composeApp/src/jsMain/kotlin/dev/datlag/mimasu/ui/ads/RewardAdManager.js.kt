package dev.datlag.mimasu.ui.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.serialization.Serializable

@Serializable
actual class RewardAdManager {
    actual fun showRewardAd(onRewarded: () -> Unit) {
        onRewarded()
    }
}

@Composable
actual fun rememberAdManager(): RewardAdManager {
    return remember { RewardAdManager() }
}