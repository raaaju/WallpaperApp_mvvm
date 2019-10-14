package com.georgcantor.wallpaperapp.util

import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.data.pixabay.Hit
import com.georgcantor.wallpaperapp.model.data.abyss.Wallpaper
import com.georgcantor.wallpaperapp.model.data.pexels.Photo
import com.georgcantor.wallpaperapp.model.data.unsplash.Result

class PicturesMapper {

    companion object {

        fun mergeResponses(
                hits: List<Hit>,
                results: List<Result>,
                walls: List<Wallpaper>,
                photos: List<Photo>
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
                                it.urlImage,
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

            photos.map {
                    pictures.add(
                            CommonPic(
                                    it.src.takeIf { it != null }?.medium,
                                    it.width,
                                    it.height,
                                    217,
                                    328,
                                    "auto, automobile",
                                    3846,
                                    it.src.takeIf { it != null }?.original,
                                    it.src.takeIf { it != null }?.large,
                                    it.photographer,
                                    it.id,
                                    it.src.takeIf { it != null }?.small
                            )
                    )
            }

            return pictures.shuffled() as ArrayList<CommonPic>
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

        fun mergeCategories(
                hits:  List<Result>,
                hits2:  List<Result>,
                hits3:  List<Result>,
                hits4:  List<Result>,
                hits5:  List<Result>,
                hits6:  List<Result>,
                hits7:  List<Result>,
                hits8:  List<Result>
        ): ArrayList<Category> {

            val categories = ArrayList<Category>()

            hits[0].urls.takeIf { it != null }?.regular?.let { Category("Animals", it) }?.let(categories::add)
            hits2[0].urls.takeIf { it != null }?.regular?.let { Category("Textures", it) }?.let(categories::add)
            hits3[0].urls.takeIf { it != null }?.regular?.let { Category("Buildings", it) }?.let(categories::add)
            hits4[0].urls.takeIf { it != null }?.regular?.let { Category("Nature", it) }?.let(categories::add)
            hits5[0].urls.takeIf { it != null }?.regular?.let { Category("Music", it) }?.let(categories::add)
            hits6[0].urls.takeIf { it != null }?.regular?.let { Category("Travel", it) }?.let(categories::add)
            hits7[0].urls.takeIf { it != null }?.regular?.let { Category("Business", it) }?.let(categories::add)
            hits8[0].urls.takeIf { it != null }?.regular?.let { Category("Fashion", it) }?.let(categories::add)

            return categories
        }

        fun mergeCategories2(
                hits: List<Hit>,
                hits2: List<Hit>,
                hits3: List<Hit>,
                hits4: List<Hit>,
                hits5: List<Hit>,
                hits6: List<Hit>,
                hits7: List<Hit>,
                hits8: List<Hit>
        ): ArrayList<Category> {

            val categories = ArrayList<Category>()

            categories.add(Category("Computer", hits[0].webformatURL))
            categories.add(Category("Feelings", hits2[0].webformatURL))
            categories.add(Category("Food", hits3[0].webformatURL))
            categories.add(Category("Health", hits4[0].webformatURL))
            categories.add(Category("People", hits5[0].webformatURL))
            categories.add(Category("Places", hits6[0].webformatURL))
            categories.add(Category("Science", hits7[0].webformatURL))
            categories.add(Category("Sports", hits8[0].webformatURL))

            return categories
        }

        fun mergeCategories(categories: List<Category>,
                            categories2: List<Category>): List<Category> = categories.plus(categories2)

    }

}