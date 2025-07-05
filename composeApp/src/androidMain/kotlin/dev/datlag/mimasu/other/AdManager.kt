package dev.datlag.mimasu.other

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.jet.ads.common.controller.JetAdsControl
import com.jet.ads.common.initializers.AdsInitializeFactory
import com.jet.ads.common.initializers.AdsInitializer
import dev.datlag.mimasu.AdActivity
import dev.datlag.mimasu.ui.common.findActivity
import dev.datlag.tooling.Platform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdManager(private val context: Context) : JetAdsControl {

    private val consentInfo = UserMessagingPlatform.getConsentInformation(context)
    private val consentParams = ConsentRequestParameters.Builder().build()

    val privacyRequired: Boolean
        get() = consentInfo.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    val consentToRequestAds: Boolean
        get() = consentInfo.canRequestAds()

    private val fallbackInitializer by lazy {
        AdsInitializeFactory.admobInitializer()
    }

    val adsPermitted
        get() = !Platform.isTelevision(context) && !Platform.isWatch(context)

    private val _adsInitialized = MutableStateFlow(false)
    val adsInitialized = _adsInitialized.asStateFlow()

    private val _adsEnabled by lazy {
        MutableStateFlow(adsInitialized.value)
    }
    private val adsEnabled by lazy {
        _adsEnabled.asStateFlow()
    }

    init {
        if (adsPermitted) {
            if (!privacyRequired || consentToRequestAds) {
                initializeAds()
            }
        }
    }

    fun initializeAds(
        activity: Activity? = context.findActivity(),
        force: Boolean = false
    ) {
        if (adsPermitted) {
            if (force || consentToRequestAds) {
                initializeSdk()

                val safeActivity = activity ?: context.findActivity()
                when {
                    safeActivity is AdActivity -> safeActivity.initAds(this)
                    safeActivity is ComponentActivity && safeActivity is AdsInitializer -> with(safeActivity as AdsInitializer) {
                        safeActivity.initializeAds(this@AdManager)
                    }
                    safeActivity is ComponentActivity -> with(fallbackInitializer) {
                        safeActivity.initializeAds(this@AdManager)
                    }
                }
            }
        }
    }

    fun requestConsentUpdate(activity: Activity? = context.findActivity()) {
        if (adsPermitted) {
            val safeActivity = activity ?: context.findActivity() ?: return

            consentInfo.requestConsentInfoUpdate(
                safeActivity,
                consentParams,
                { // success
                    showConsentFormIfRequired(safeActivity) {
                        initializeAds(safeActivity)
                    }
                },
                { failure ->
                    initializeAds(safeActivity, force = true)
                }
            )
        }
    }

    private fun showConsentFormIfRequired(
        activity: Activity? = context.findActivity(),
        onDismiss: () -> Unit
    ) {
        if (adsPermitted) {
            val safeActivity = activity ?: context.findActivity() ?: return

            UserMessagingPlatform.loadAndShowConsentFormIfRequired(safeActivity) {
                onDismiss()
            }
        }
    }

    private fun initializeSdk() {
        if (adsInitialized.value || !adsPermitted) {
            return
        }

        MobileAds.initialize(context) {
            _adsInitialized.update { true }
            setAdsEnabled(true)
        }
    }

    override fun isAdsEnabled(): StateFlow<Boolean> {
        return adsEnabled
    }

    override fun setAdsEnabled(enabled: Boolean) {
        _adsEnabled.update { enabled }
    }
}