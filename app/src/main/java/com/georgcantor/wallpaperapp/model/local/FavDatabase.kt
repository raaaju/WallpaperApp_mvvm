package com.georgcantor.wallpaperapp.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Favorite::class], version = DatabaseMigration.latestVersion, exportSchema = false)
abstract class FavDatabase : RoomDatabase() {

    companion object {
        private const val dbName = "fav_db"

        fun buildDefault(context: Context) =
                Room.databaseBuilder(context, FavDatabase::class.java, dbName)
                        .addMigrations(*DatabaseMigration.allMigrations)
                        .build()
    }

    abstract fun dao(): FavDao

}