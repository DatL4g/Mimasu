package dev.datlag.mimasu.other

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.jet.ads.common.controller.JetAdsAdsControlImpl
import com.jet.ads.common.initializers.AdsInitializeFactory
import dev.datlag.mimasu.common.findActivity
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdManager(private val context: Context) {

    private var mobileAdsInitialized by atomic(false)
    private var jetAdsInitialized by atomic(false)

    private val consentInfo = UserMessagingPlatform.getConsentInformation(context)
    private val params = ConsentRequestParameters.Builder().build()

    val privacyRequired: Boolean
        get() = consentInfo.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    private val consentToRequestAds: Boolean
        get() = consentInfo.canRequestAds()

    private val _canRequestAds = MutableStateFlow(consentToRequestAds && mobileAdsInitialized)
    val canRequestAds = _canRequestAds.asStateFlow()

    val initializer = AdsInitializeFactory.admobInitializer()

    init {
        if (!privacyRequired) {
            initializeMobileAds()
        }
    }

    fun requestUpdate(
        activity: Activity? = context.findActivity(),
        onUpdated: () -> Unit,
        onFailure: () -> Unit
    ) {
        val safeActivity = activity ?: context.findActivity() ?: return

        consentInfo.requestConsentInfoUpdate(
            safeActivity,
            params,
            { // success
                onUpdated()
            },
            { failure ->
                onFailure()
            }
        )
    }

    fun showFormIfRequired(
        activity: Activity? = context.findActivity(),
        onDismiss: () -> Unit
    ) {
        val safeActivity = activity ?: context.findActivity() ?: return

        UserMessagingPlatform.loadAndShowConsentFormIfRequired(safeActivity) {
            onDismiss()
        }
    }

    fun showPrivacyForm(
        activity: Activity? = context.findActivity(),
        onDismiss: () -> Unit
    ) {
        val safeActivity = activity ?: context.findActivity() ?: return

        UserMessagingPlatform.showPrivacyOptionsForm(safeActivity) {
            onDismiss()
        }
    }

    fun initializeMobileAds(
        activity: Activity? = context.findActivity(),
        force: Boolean = false,
        afterInitialize: () -> Unit = { }
    ) {
        if (consentToRequestAds || force) {
            if (mobileAdsInitialized) {
                if (jetAdsInitialized) with(initializer) {
                    val safeActivity = (activity as? ComponentActivity) ?: context.findActivity() as? ComponentActivity
                    safeActivity?.initializeAds()?.also {
                        jetAdsInitialized = true
                    }
                }
                return afterInitialize()
            }

            MobileAds.initialize(context) {
                mobileAdsInitialized = true
                _canRequestAds.update { consentToRequestAds && mobileAdsInitialized }
                if (jetAdsInitialized) with(initializer) {
                    val safeActivity = (activity as? ComponentActivity) ?: context.findActivity() as? ComponentActivity
                    safeActivity?.initializeAds()?.also {
                        jetAdsInitialized = true
                    }
                }
                afterInitialize()
            }
        }
    }


}