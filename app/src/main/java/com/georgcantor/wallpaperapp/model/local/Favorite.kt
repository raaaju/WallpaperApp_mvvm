package com.georgcantor.wallpaperapp.model.local

class Favorite {

    var id: Int = 0
    var imageUrl: String? = null
    var hdUrl: String? = null
    var hit: String? = null

    companion object {
        const val TABLE_NAME = "favorite"
        const val COLUMN_ID = "id"
        const val COLUMN_URL = "imageUrl"
        const val COLUMN_HD_URL = "hdUrl"
        const val COLUMN_HIT = "hit"

        const val CREATE_TABLE = ("CREATE TABLE "
                + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_URL + " TEXT,"
                + COLUMN_HD_URL + " TEXT,"
                + COLUMN_HIT + " TEXT"
                + ")")
    }
}
