package com.georgcantor.wallpaperapp.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite): Long

    @Query("DELETE FROM favorites WHERE url = :url")
    suspend fun deleteByUrl(url: String?)

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()

    @Query("SELECT * FROM favorites WHERE url LIKE :url")
    suspend fun getByUrl(url: String): List<Favorite>

    @Query("SELECT * FROM favorites")
    suspend fun getAll(): List<Favorite>

    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE url = :url)")
    suspend fun isFavorite(url: String?): Boolean
}