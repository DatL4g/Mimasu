package dev.datlag.mimasu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.datlag.mimasu.firebase.firestore.UserData
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import kotlinx.serialization.Serializable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.extension.model.Show
import dev.datlag.mimasu.ui.ads.RewardAdManager
import dev.datlag.mimasu.ui.ads.rememberAdManager
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel

@Serializable
data class VideoNavigationController(
    val userData: UserData?,
    val rewardAdManager: RewardAdManager
) {

    fun loadSources(
        data: VideoViewModel.WatchData,
        navigate: () -> Unit
    ) {
        val canNavigate = VideoViewModel.watch(data)

        if (canNavigate) {
            if (userData?.premium == true) {
                navigate()
            } else {
                rewardAdManager.showRewardAd(navigate)
            }
        } else {
            VideoViewModel.clear()
        }
    }
}

@Composable
fun rememberVideoNavigationController(): VideoNavigationController {
    val accountViewModel = accountViewModel()
    val userData by accountViewModel.userData.collectAsStateWithLifecycle()
    val rewardAdManager = rememberAdManager()

    return remember(userData, rewardAdManager) {
        VideoNavigationController(userData, rewardAdManager)
    }
}