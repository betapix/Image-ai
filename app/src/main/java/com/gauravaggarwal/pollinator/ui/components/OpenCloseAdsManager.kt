/*
 * This file is part of Pollinator.
 */
package com.gauravaggarwal.pollinator.ui.components

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.appnext.ads.interstitial.Interstitial
import com.google.android.gms.ads.appopen.AppOpenAd

class OpenCloseAdsManager(private val application: Application, private val appnextPlacementId: String) : DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null
    private var appnextInterstitial: Interstitial? = null
    private val admobAppOpen by lazy { AppOpenAdManager(application) }
    private val admobInterstitial by lazy { InterstitialAdManager(application) }

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        // Preload both networks
        preloadAppnext()
        admobAppOpen.loadAd()
        admobInterstitial.loadAd()
    }

    private fun preloadAppnext() {
        val act = currentActivity ?: return
        val inter = Interstitial(act, appnextPlacementId)
        appnextInterstitial = inter
        try {
            inter.loadAd()
        } catch (_: Exception) { /* ignore */ }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // App moved to foreground → try Appnext interstitial, fallback to AdMob AppOpen
        showOnAppOpen()
    }

    fun showOnAppOpen() {
        val act = currentActivity ?: return
        val appnext = appnextInterstitial
        try {
            if (appnext != null && appnext.isAdLoaded) {
                appnext.showAd()
                // Preload next after show
                preloadAppnext()
                return
            }
        } catch (_: Exception) { /* fallback */ }
        // Fallback to AdMob AppOpen
        admobAppOpen.showIfAvailable()
    }

    fun showOnClose(onAfter: () -> Unit) {
        val appnext = appnextInterstitial
        val act = currentActivity
        try {
            if (act != null && appnext != null && appnext.isAdLoaded) {
                appnext.showAd()
                preloadAppnext()
                onAfter()
                return
            }
        } catch (_: Exception) { }
        // Fallback to AdMob interstitial
        if (admobInterstitial.isAdLoaded()) {
            admobInterstitial.showAd {
                onAfter()
            }
        } else {
            onAfter()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { currentActivity = activity }
    override fun onActivityStarted(activity: Activity) { currentActivity = activity }
    override fun onActivityResumed(activity: Activity) { currentActivity = activity }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) { if (currentActivity === activity) currentActivity = null }
}


