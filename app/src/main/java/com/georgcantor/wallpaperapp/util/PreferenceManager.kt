package com.georgcantor.wallpaperapp.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.georgcantor.wallpaperapp.ui.DetailsActivity.Companion.MY_PREFS

class PreferenceManager(activity: Activity) {

    private val prefs: SharedPreferences = activity.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)

    fun saveBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()

    fun saveInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()


    fun getBoolean(key: String): Boolean = prefs.getBoolean(key, false)

    fun getInt(key: String): Int = prefs.getInt(key, 0)

}