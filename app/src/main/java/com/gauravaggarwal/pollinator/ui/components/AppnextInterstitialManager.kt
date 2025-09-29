package com.gauravaggarwal.pollinator.ui.components

import android.content.Context
import com.appnext.ads.interstitial.Interstitial

class AppnextInterstitialManager(private val context: Context, private val placementId: String) {
    private var interstitial: Interstitial? = null

    fun loadAd() {
        val ad = Interstitial(context, placementId)
        interstitial = ad
        try {
            ad.loadAd()
        } catch (_: Exception) { }
    }

    fun isAdLoaded(): Boolean {
        return try { interstitial?.isAdLoaded == true } catch (_: Exception) { false }
    }

    fun showAd(onClosed: () -> Unit = {}) {
        val ad = interstitial
        try {
            if (ad != null && ad.isAdLoaded) {
                ad.showAd()
                // Fire callback immediately after show (Appnext SDK closes quickly after interaction)
                onClosed()
                loadAd()
            } else {
                onClosed()
                loadAd()
            }
        } catch (_: Exception) {
            onClosed()
            loadAd()
        }
    }
}


