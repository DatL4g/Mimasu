package dev.datlag.mimasu.ui.ads

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
import dev.datlag.mimasu.other.AdManager
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull
import kotlin.getValue

@Composable
actual fun BannerAd(modifier: Modifier) = with(localDI()) {
    val nullableAdManager by instanceOrNull<AdManager>()
    val context = LocalContext.current
    val adManager = remember(nullableAdManager, context) { nullableAdManager ?: AdManager(context) }
    val canRequestAds by adManager.canRequestAds.collectAsStateWithLifecycle()
    var display by remember { mutableStateOf(true) }

    if (canRequestAds && display) {
        AdaptiveBanner(
            adUnit = AdMobTestIds.ADAPTIVE_BANNER,
            modifier = modifier,
            safeTopMarginDp = 0.dp,
            bannerCallBack = BannerCallBack(
                onAdLoaded = {
                    display = true
                },
                onAdFailedToLoad = {
                    display = false
                }
            )
        )
    }
}