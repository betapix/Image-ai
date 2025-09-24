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

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AppOpenAdManager(private val application: Application) : DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {
    
    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    private val adUnitId = "ca-app-pub-3940256099942544/3419835294" // Test Ad Unit ID
    
    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
    
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // Show ad when app comes to foreground
        showIfAvailable()
    }
    
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }
    
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }
    
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }
    
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }
    
    fun loadAd() {
        // Don't load ad if there is already an unused ad or one is currently loading
        if (isAdAvailable() || isLoading) {
            return
        }
        
        isLoading = true
        val request = AdRequest.Builder().build()
        
        AppOpenAd.load(
            application,
            adUnitId,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoading = false
                    loadTime = Date().time
                    // If app is already in foreground, try showing immediately
                    if (!isShowingAd) {
                        showIfAvailable()
                    }
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoading = false
                }
            }
        )
    }
    
    fun showIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            currentActivity?.let { activity ->
                showAd(activity)
            }
        } else {
            // If no ad is available, load a new one
            loadAd()
        }
    }
    
    private fun showAd(activity: Activity) {
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }
            
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        
        isShowingAd = true
        appOpenAd?.show(activity)
    }
    
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }
    
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 60 * 60 * 1000
        return dateDifference < numMilliSecondsPerHour * numHours
    }
    
    companion object {
        private var isLoading = false
        private var isShowingAd = false
    }
}
