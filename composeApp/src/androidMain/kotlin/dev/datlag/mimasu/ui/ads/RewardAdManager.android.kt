package dev.datlag.mimasu.ui.ads

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jet.ads.admob.AdMobTestIds
import com.jet.ads.common.callbacks.ShowAdCallBack
import com.jet.ads.common.rewarded.RewardedControllerFactory
import com.jet.ads.common.rewarded.RewardsController
import dev.datlag.mimasu.BuildConfig
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.other.AdManager
import dev.datlag.mimasu.ui.common.findActivity
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Serializable
actual class RewardAdManager(
    @Transient private val activity: Activity? = null,
    private val rewardManager: RewardsController = RewardedControllerFactory.admobController(),
    private val available: Boolean
) {

    actual fun showRewardAd(
        onRewarded: () -> Unit
    ) {
        if (activity != null && available) {
            val unitId = if (BuildConfig.DEBUG) {
                AdMobTestIds.REWARDED
            } else {
                Sekret.admobVideoReward(BuildKonfig.packageName)?.ifBlank { null }
            }

            if (unitId != null) {
                rewardManager.show(
                    adUnitId = unitId,
                    activity = activity,
                    callBack = ShowAdCallBack(
                        onAdFailedToShow = {
                            onRewarded()
                        }
                    ),
                    onRewarded = {
                        onRewarded()
                    }
                )
            } else {
                onRewarded()
            }
        } else {
            onRewarded()
        }
    }
}

@Composable
actual fun rememberAdManager(): RewardAdManager = with(localDI()) {
    val nullableAdManager by instanceOrNull<AdManager>()
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val adManager = remember(nullableAdManager, context) {
        nullableAdManager ?: AdManager(context)
    }
    val adsInitialized by adManager.adsInitialized.collectAsStateWithLifecycle()
    val displayAd by remember(adManager) { mutableStateOf(adManager.adsPermitted) }

    LaunchedEffect(adManager) {
        adManager.initializeAds(activity)
    }

    return RewardAdManager(
        activity = activity,
        available = adsInitialized && displayAd
    )
}