package com.georgcantor.wallpaperapp.ui.util

import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.PicUrl
import com.georgcantor.wallpaperapp.model.unsplash.Result

class PicturesMapper {

    companion object {
        fun mergeResponses(hits: List<Hit>, results: List<Result>): ArrayList<PicUrl> {
            val strings = ArrayList<PicUrl>()

            results.map {
                it.urls.takeUnless { urls ->
                    strings.add(PicUrl(urls?.small, it.width ?: 0, it.height ?: 0))
                }
            }

            hits.map {
                strings.add(PicUrl(it.webformatURL, it.imageWidth, it.imageHeight))
            }

            return strings
        }
    }

}