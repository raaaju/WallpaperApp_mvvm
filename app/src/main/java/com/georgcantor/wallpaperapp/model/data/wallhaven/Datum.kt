package com.georgcantor.wallpaperapp.model.data.wallhaven

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Datum {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("short_url")
    @Expose
    var shortUrl: String? = null

    @SerializedName("views")
    @Expose
    var views: Int? = null

    @SerializedName("favorites")
    @Expose
    var favorites: Int? = null

    @SerializedName("source")
    @Expose
    var source: String? = null

    @SerializedName("purity")
    @Expose
    var purity: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("dimension_x")
    @Expose
    var dimensionX: Int? = null

    @SerializedName("dimension_y")
    @Expose
    var dimensionY: Int? = null

    @SerializedName("resolution")
    @Expose
    var resolution: String? = null

    @SerializedName("ratio")
    @Expose
    var ratio: String? = null

    @SerializedName("file_size")
    @Expose
    var fileSize: Int? = null

    @SerializedName("file_type")
    @Expose
    var fileType: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("colors")
    @Expose
    var colors: List<String>? = null

    @SerializedName("path")
    @Expose
    var path: String? = null

    @SerializedName("thumbs")
    @Expose
    var thumbs: Thumbs? = null
}