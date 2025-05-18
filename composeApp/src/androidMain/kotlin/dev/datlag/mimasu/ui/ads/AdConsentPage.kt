package dev.datlag.mimasu.ui.ads

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.common.findActivity
import dev.datlag.mimasu.other.AdManager
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Composable
fun AdConsentPage(
    content: @Composable () -> Unit
) = with(localDI()) {
    val nullableAdManager by instanceOrNull<AdManager>()
    val context = LocalContext.current
    val adManager = remember(nullableAdManager, context) { nullableAdManager ?: AdManager(context) }
    val activity = LocalActivity.current ?: context.findActivity()
    val canRequestAds by adManager.canRequestAds.collectAsStateWithLifecycle()
    var updated by remember(adManager) { mutableStateOf(false) }

    LaunchedEffect(adManager) {
        adManager.requestUpdate(
            activity = activity,
            onUpdated = {
                updated = true
            },
            onFailure = {
                adManager.initializeMobileAds(force = true)
            }
        )
    }
    LaunchedEffect(canRequestAds, updated) {
        if (!canRequestAds && updated) {
            adManager.showFormIfRequired(activity) {
                adManager.initializeMobileAds()
            }
        }
    }
    content()
}