/*
 * This file is part of Pollinator.
 *
 * Pollinator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pollinator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pollinator. If not, see <https://www.gnu.org/licenses/>.
 */
package com.gauravaggarwal.pollinator.ui.components

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAdManager(private val context: Context) {
    private var rewardedAd: RewardedAd? = null
    private val adUnitId = "ca-app-pub-3940256099942544/5224354917" // Test Rewarded Ad Unit ID
    private val appnextPlacementId = "8546bc6c-79c9-4051-9194-e2e7d46a4d67"
    private val appnextInterstitial by lazy { AppnextInterstitialManager(context, appnextPlacementId) }

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
            }
        )
        // Also preload Appnext as fallback
        appnextInterstitial.loadAd()
    }

    fun showAd(onRewardEarned: () -> Unit, onAdClosed: () -> Unit = {}) {
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    onAdClosed()
                    // Load next ad
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    // Try Appnext fallback
                    if (appnextInterstitial.isAdLoaded()) {
                        appnextInterstitial.showAd {
                            onAdClosed()
                        }
                    } else {
                        onAdClosed()
                    }
                }
            }
            
            ad.show(context as androidx.activity.ComponentActivity) { rewardItem ->
                // User earned reward
                onRewardEarned()
            }
        } ?: run {
            // AdMob not ready → try Appnext fallback
            if (appnextInterstitial.isAdLoaded()) {
                appnextInterstitial.showAd {
                    onAdClosed()
                }
            } else {
                onAdClosed()
            }
        }
    }

    fun isAdLoaded(): Boolean {
        return rewardedAd != null
    }
}
