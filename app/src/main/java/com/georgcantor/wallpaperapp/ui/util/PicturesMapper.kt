package com.georgcantor.wallpaperapp.ui.util

import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.model.abyss.Wallpaper
import com.georgcantor.wallpaperapp.model.unsplash.Result

class PicturesMapper {

    companion object {

        fun mergeResponses(
            hits: List<Hit>,
            results: List<Result>,
            walls: List<Wallpaper>
        ): ArrayList<CommonPic> {

            val pictures = ArrayList<CommonPic>()

            results.map {
                it.urls.takeUnless { urls ->
                    pictures.add(
                        CommonPic(
                            urls?.small,
                            it.width ?: 0,
                            it.height ?: 0,
                            it.likes ?: 365,
                            542,
                            "car, auto",
                            5923,
                            urls?.full,
                            urls?.regular,
                            "George Smith",
                            it.hashCode(),
                            urls?.thumb
                        )
                    )
                }
            }

            walls.map {
                pictures.add(
                    CommonPic(
                        it.urlThumb,
                        it.width?.toInt() ?: 0,
                        it.height?.toInt() ?: 0,
                        481,
                        542,
                        "car, auto",
                        4245,
                        it.urlImage,
                        it.urlThumb,
                        "Mike Antony",
                        it.id?.toInt() ?: it.hashCode(),
                        it.urlThumb
                    )
                )
            }

            hits.map {
                pictures.add(
                    CommonPic(
                        it.webformatURL,
                        it.imageWidth,
                        it.imageHeight,
                        it.likes,
                        it.favorites,
                        it.tags,
                        it.downloads,
                        it.imageURL,
                        it.webformatURL,
                        it.user,
                        it.id,
                        it.userImageURL
                    )
                )
            }

            return pictures
        }

        fun convertResponse(hits: List<Hit>): ArrayList<CommonPic> {
            val pictures = ArrayList<CommonPic>()

            hits.map {
                pictures.add(
                    CommonPic(
                        it.webformatURL,
                        it.imageWidth,
                        it.imageHeight,
                        it.likes,
                        it.favorites,
                        it.tags,
                        it.downloads,
                        it.imageURL,
                        it.webformatURL,
                        it.user,
                        it.id,
                        it.userImageURL
                    )
                )
            }

            return pictures
        }

    }

}