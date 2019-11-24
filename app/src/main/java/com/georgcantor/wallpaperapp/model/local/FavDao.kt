package com.georgcantor.wallpaperapp.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favorite: Favorite): Long

    @Query("DELETE FROM favorites WHERE url = :url")
    fun deleteByUrl(url: String)

    @Query("DELETE FROM favorites")
    fun deleteAll()

    @Query("SELECT * FROM favorites WHERE url LIKE :url")
    fun getByUrl(url: String): List<Favorite>

    @Query("SELECT * FROM favorites")
    fun getAll(): List<Favorite>

}