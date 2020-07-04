package com.georgcantor.wallpaperapp.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.util.Constants.MY_PREFS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceManager(context: Context) {

    private val gson = Gson()
    private var json = ""

    private val prefs: SharedPreferences = context.getSharedPreferences(MY_PREFS, MODE_PRIVATE)

    fun saveBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()

    fun saveString(key: String, value: String) = prefs.edit().putString(key, value).apply()

    fun saveInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()

    fun saveCategories(key: String, categories: ArrayList<Category>) {
        json = gson.toJson(categories)
        prefs.edit().putString(key, json).apply()
    }

    fun getBoolean(key: String): Boolean = prefs.getBoolean(key, false)

    fun getString(key: String): String? = prefs.getString(key, "")

    fun getInt(key: String): Int = prefs.getInt(key, 0)

    fun getCategories(key: String): ArrayList<Category>? {
        val type = object : TypeToken<ArrayList<Category>>() {}.type
        val json = prefs.getString(key, "")

        return gson.fromJson(json, type)
    }
}