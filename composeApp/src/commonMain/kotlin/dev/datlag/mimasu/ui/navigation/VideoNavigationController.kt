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
        sources: Map<Show.Response.SourceInfo, Collection<String>>,
        navigate: () -> Unit
    ) {
        val canNavigate = VideoViewModel.updateSources(sources.map { (k, v) ->
            VideoViewModel.SourceInfo(
                sourceTitle = k.sourceTitle?.ifBlank { null },
                sourceKey = k.sourceKey?.ifBlank { null },
                locale = k.locale?.ifBlank { null }
            ) to v
        }.toMap())

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