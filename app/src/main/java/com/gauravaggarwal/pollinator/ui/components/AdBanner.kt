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
import android.util.DisplayMetrics
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.appnext.banners.BannerAd
import com.appnext.banners.BannerListener
import com.appnext.banners.BannerSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111"
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var showAdMob by remember { mutableStateOf(false) }
    var adView by remember { mutableStateOf<AdView?>(null) }
    var adHeightDp by remember { mutableStateOf(0) }
    var reloadKey by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    fun computeAdaptiveAdSize(): AdSize? {
        val act = activity ?: return null
        val displayMetrics = DisplayMetrics()
        act.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val density = displayMetrics.density
        val adWidthPixels = displayMetrics.widthPixels
        val adWidthDp = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(act, adWidthDp)
    }

    LaunchedEffect(reloadKey) {
        // Try Appnext first
        if (!showAdMob && activity != null) {
            val appnextBanner = BannerAd(activity)
            appnextBanner.setPlacementId("8546bc6c-79c9-4051-9194-e2e7d46a4d67")
            appnextBanner.setBannerSize(BannerSize.BANNER)
            appnextBanner.setBannerListener(object : BannerListener() {
                override fun onAdLoaded() {
                    // Render Appnext banner via AndroidView
                }
                override fun onError(error: String?) {
                    showAdMob = true
                }
            })
            try { appnextBanner.loadAd() } catch (_: Exception) { showAdMob = true }
        } else {
            showAdMob = true
        }

        if (!showAdMob) return@LaunchedEffect

        val size = computeAdaptiveAdSize() ?: return@LaunchedEffect
        adHeightDp = (size.getHeightInPixels(context) / context.resources.displayMetrics.density).toInt()

        val view = AdView(context).apply {
            setAdSize(size)
            this.adUnitId = adUnitId
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    scope.launch {
                        delay(2000)
                        reloadKey++
                    }
                }
            }
        }
        adView = view
        view.loadAd(AdRequest.Builder().build())
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!showAdMob) {
            AndroidView(
                factory = { ctx ->
                    val banner = BannerAd(activity)
                    banner.setPlacementId("8546bc6c-79c9-4051-9194-e2e7d46a4d67")
                    banner.setBannerSize(BannerSize.BANNER)
                    banner
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }

        adView?.let { ad ->
            val height = if (adHeightDp > 0) adHeightDp.dp else 50.dp
            AndroidView(
                factory = { ad },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            )
        }
    }
}
