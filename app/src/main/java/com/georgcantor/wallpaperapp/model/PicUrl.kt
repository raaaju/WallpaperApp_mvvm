package com.georgcantor.wallpaperapp.model

import android.os.Parcel
import android.os.Parcelable

data class PicUrl(
    val url: String?,
    val width: Int,
    val heght: Int
//    var previewHeight: Int = 0,
//    var likes: Int = 0,
//    var favorites: Int = 0,
//    var tags: String? = null,
//    var webformatHeight: Int = 0,
//    var views: Int = 0,
//    var webformatWidth: Int = 0,
//    var previewWidth: Int = 0,
//    var comments: Int = 0,
//    var downloads: Int = 0,
//    var pageURL: String? = null,
//    var previewURL: String? = null,
//    var webformatURL: String? = null,
//    var imageURL: String? = null,
//    var fullHDURL: String? = null,
//    var imageWidth: Int = 0,
//    var userId: Int = 0,
//    var user: String? = null,
//    var type: String? = null,
//    var id: Int = 0,
//    var userImageURL: String? = null,
//    var imageHeight: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readString(),
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readInt(),
//        parcel.readString(),
//        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeInt(width)
        parcel.writeInt(heght)
//        parcel.writeInt(previewHeight)
//        parcel.writeInt(likes)
//        parcel.writeInt(favorites)
//        parcel.writeString(tags)
//        parcel.writeInt(webformatHeight)
//        parcel.writeInt(views)
//        parcel.writeInt(webformatWidth)
//        parcel.writeInt(previewWidth)
//        parcel.writeInt(comments)
//        parcel.writeInt(downloads)
//        parcel.writeString(pageURL)
//        parcel.writeString(previewURL)
//        parcel.writeString(webformatURL)
//        parcel.writeString(imageURL)
//        parcel.writeString(fullHDURL)
//        parcel.writeInt(imageWidth)
//        parcel.writeInt(userId)
//        parcel.writeString(user)
//        parcel.writeString(type)
//        parcel.writeInt(id)
//        parcel.writeString(userImageURL)
//        parcel.writeInt(imageHeight)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PicUrl> {
        override fun createFromParcel(parcel: Parcel): PicUrl = PicUrl(parcel)

        override fun newArray(size: Int): Array<PicUrl?> = arrayOfNulls(size)
    }

}