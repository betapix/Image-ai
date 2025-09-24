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
package com.gauravaggarwal.pollinator.model

private const val DEFAULT = 1024
private val sizes = listOf(
    512,
    640,
    720,
    768,
    1024,
)

class Sizes {
    companion object {
        data class SizeOption(val label: String, val value: Int)
        data class WallpaperPreset(val label: String, val width: Int, val height: Int)

        fun getSizeList(): List<Int> {
            return sizes
        }

        fun getDefaultSize(): Int {
            return DEFAULT
        }

        fun getLabeledSizeList(): List<SizeOption> {
            return listOf(
                SizeOption(label = "Logo (512)", value = 512),
                SizeOption(label = "Thumbnail (640)", value = 640),
                SizeOption(label = "Small (720)", value = 720),
                SizeOption(label = "Medium (768)", value = 768),
                SizeOption(label = "Large (1024)", value = 1024),
            )
        }

        fun getWallpaperPresets(): List<WallpaperPreset> {
            return listOf(
                WallpaperPreset(label = "Wallpaper (720x1280 • HD)", width = 720, height = 1280),
                WallpaperPreset(label = "Wallpaper (1080x2340 • FHD+)", width = 1080, height = 2340),
                WallpaperPreset(label = "Wallpaper (1080x2400 • FHD+)", width = 1080, height = 2400),
                WallpaperPreset(label = "Wallpaper (1440x2960 • QHD+)", width = 1440, height = 2960),
                WallpaperPreset(label = "Wallpaper (1440x3040 • QHD+)", width = 1440, height = 3040),
                WallpaperPreset(label = "Wallpaper (2160x3840 • 4K UHD)", width = 2160, height = 3840),
            )
        }
    }
}