package dev.datlag.mimasu.ui.ads

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jet.ads.admob.AdMobTestIds
import com.jet.ads.admob.banner.AdaptiveBanner
import com.jet.ads.common.callbacks.BannerCallBack
import dev.datlag.mimasu.BuildConfig
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.other.AdManager
import dev.datlag.mimasu.ui.common.findActivity
import dev.datlag.tooling.compose.LaunchedMain
import dev.datlag.tooling.compose.MainThread
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@OptIn(MainThread::class)
@Composable
actual fun BannerAd(type: Banner, modifier: Modifier) = with(localDI()) {
    val bannerId = remember {
        if (BuildConfig.DEBUG) {
            AdMobTestIds.ADAPTIVE_BANNER
        } else when (type) {
            is Banner.Home -> Sekret.admobHomeBanner(BuildKonfig.packageName)?.ifBlank { null }
            is Banner.Space -> Sekret.admobSpaceBanner(BuildKonfig.packageName)?.ifBlank { null }
            else -> null
        }
    } ?: return
    val nullableAdManager by instanceOrNull<AdManager>()
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val adManager = remember(nullableAdManager, context) { nullableAdManager ?: AdManager(context) }
    val adsInitialized by adManager.adsInitialized.collectAsStateWithLifecycle()
    var displayAd by remember(adManager) { mutableStateOf(adManager.adsPermitted) }

    LaunchedMain(adManager) {
        adManager.initializeAds(activity)
    }

    if (displayAd && adsInitialized) {
        AdaptiveBanner(
            adUnit = bannerId,
            modifier = modifier,
            safeTopMarginDp = 0.dp,
            bannerCallBack = BannerCallBack(
                onAdLoaded = {
                    displayAd = true
                },
                onAdFailedToLoad = {
                    displayAd = false
                }
            )
        )
    }
}