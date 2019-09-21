package com.georgcantor.wallpaperapp.ui.util

import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.PicUrl
import com.georgcantor.wallpaperapp.model.abyss.Wallpaper
import com.georgcantor.wallpaperapp.model.unsplash.Result

class PicturesMapper {

    companion object {
        fun mergeResponses(hits: List<Hit>, results: List<Result>, walls: List<Wallpaper>): ArrayList<PicUrl> {
            val pictures = ArrayList<PicUrl>()

            results.map {
                it.urls.takeUnless { urls ->
                    pictures.add(PicUrl(urls?.small, it.width ?: 0, it.height ?: 0))
                }
            }

            walls.map {
                pictures.add(PicUrl(it.urlThumb, it.width?.toInt() ?: 0, it.height?.toInt() ?: 0))
            }

            hits.map {
                pictures.add(PicUrl(it.webformatURL, it.imageWidth, it.imageHeight))
            }

            return pictures
        }
    }

}