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
package com.gauravaggarwal.pollinator

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.gauravaggarwal.pollinator.ui.components.OpenCloseAdsManager

class PollinatorApplication : Application() {
    lateinit var openCloseAdsManager: OpenCloseAdsManager
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        MobileAds.initialize(this) {}

        // Initialize open/close ads manager with Appnext placement
        openCloseAdsManager = OpenCloseAdsManager(this, "8546bc6c-79c9-4051-9194-e2e7d46a4d67")
    }
}
