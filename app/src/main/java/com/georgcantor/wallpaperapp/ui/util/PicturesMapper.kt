package com.georgcantor.wallpaperapp.ui.util

import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.PicUrl
import com.georgcantor.wallpaperapp.model.unsplash.Result

class PicturesMapper {

    companion object {
        fun concatsResponses(hits: List<Hit>, results: List<Result>): ArrayList<PicUrl> {
            val strings = ArrayList<PicUrl>()

            results.forEach {
                it.urls.takeUnless { urls ->
                    strings.add(PicUrl(urls?.small!!, it.width!!, it.height!!))
                }
            }

            hits.map {
                strings.add(PicUrl(it.webformatURL, it.imageWidth, it.imageHeight))
            }

            return strings
        }
    }

}