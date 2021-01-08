package com.georgcantor.wallpaperapp.model.local

import androidx.room.migration.Migration

internal object DatabaseMigration {

    const val latestVersion = 1

    val allMigrations: Array<Migration>
        get() = arrayOf()

    object V1 {
        object Favorite {
            const val tableName = "favorites"

            object Column {
                const val id = "id"
                const val hit = "hit"
            }
        }
    }
}