package com.georgcantor.wallpaperapp.model.db

import android.content.ContentValues

import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "favorite_db"
    }

    val allFavorites: List<Favorite>
        get() {
            val favoriteList = ArrayList<Favorite>()

            val selectQuery = "SELECT  * FROM " + Favorite.TABLE_NAME + " ORDER BY " +
                    Favorite.COLUMN_TIMESTAMP + " DESC"

            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val favorite = Favorite()
                    favorite.id = cursor.getInt(cursor.getColumnIndex(Favorite.COLUMN_ID))
                    favorite.imageUrl = cursor.getString(cursor.getColumnIndex(Favorite.COLUMN_URL))
                    favorite.hdUrl = cursor.getString(cursor.getColumnIndex(Favorite.COLUMN_HD_URL))
                    favorite.timestamp = cursor.getString(cursor.getColumnIndex(Favorite.COLUMN_TIMESTAMP))

                    favoriteList.add(favorite)
                } while (cursor.moveToNext())
            }
            db.close()

            return favoriteList
        }

    val historyCount: Int
        get() {
            val countQuery = "SELECT  * FROM " + Favorite.TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()

            return count
        }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Favorite.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + Favorite.TABLE_NAME)
        onCreate(db)
    }

    fun insertToFavorites(imageUrl: String, hdUrl: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(Favorite.COLUMN_URL, imageUrl)
        values.put(Favorite.COLUMN_HD_URL, hdUrl)

        val id = db.insert(Favorite.TABLE_NAME, null, values)
        db.close()

        return id
    }

    fun deleteFromFavorites(imageUrl: String) {
        val db = this.writableDatabase
        db.delete(Favorite.TABLE_NAME, Favorite.COLUMN_URL + " = ?", arrayOf(imageUrl))
        db.close()
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.delete(Favorite.TABLE_NAME, null, null)
    }

    fun containFav(imageUrl: String): Boolean {
        val db = this.readableDatabase
        val query = ("SELECT  * FROM " + Favorite.TABLE_NAME + " WHERE "
                + Favorite.COLUMN_URL + " = " + DatabaseUtils.sqlEscapeString(imageUrl))
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToNext()) {
            cursor.close()

            return true
        }

        return false
    }
}

