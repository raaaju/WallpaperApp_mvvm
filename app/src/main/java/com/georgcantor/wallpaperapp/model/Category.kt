package com.georgcantor.wallpaperapp.model

class Category(categoryName: String, categoryDrawId: String) {

    var categoryName: String? = null
    var categoryDrawId: String? = null

    init {
        this.categoryName = categoryName
        this.categoryDrawId = categoryDrawId
    }
}
