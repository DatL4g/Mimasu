package dev.datlag.mimasu

import androidx.activity.ComponentActivity
import com.jet.ads.common.controller.JetAdsAdsControlImpl
import com.jet.ads.common.controller.JetAdsControl
import com.jet.ads.common.initializers.AdsInitializeFactory
import com.jet.ads.common.initializers.AdsInitializer

open class AdActivity : ComponentActivity(), AdsInitializer by AdsInitializeFactory.admobInitializer() {

    @JvmOverloads
    fun initAds(adsControl: JetAdsControl = JetAdsAdsControlImpl) {
        initializeAds(adsControl)
    }
}