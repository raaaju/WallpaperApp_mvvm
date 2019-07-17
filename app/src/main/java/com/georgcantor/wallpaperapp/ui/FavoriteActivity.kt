package com.georgcantor.wallpaperapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.GridView
import android.widget.TextView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.db.Favorite
import com.georgcantor.wallpaperapp.ui.adapter.FavoriteAdapter
import java.util.*

class FavoriteActivity : AppCompatActivity() {

    private var list: ArrayList<Favorite>? = null
    private var db: DatabaseHelper? = null
    private var textViewNoFav: TextView? = null
    private var gridView: GridView? = null
    private var adapter: FavoriteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_fav)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.favorites)

        gridView = findViewById(R.id.gridViewFav)
        textViewNoFav = findViewById(R.id.empty_fav_tv)

        db = DatabaseHelper(this)
        list = ArrayList()
        db?.allFavorites?.let { list?.addAll(it) }

        list?.let { adapter = FavoriteAdapter(this, R.layout.favorite_list_row, it) }
        gridView?.adapter = adapter

        toggleEmptyHistory()
    }

    private fun toggleEmptyHistory() {
        db?.let {
            if (it.historyCount > 0) {
                textViewNoFav?.visibility = View.GONE
            } else {
                textViewNoFav?.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
        }

        return super.onOptionsItemSelected(item)
    }
}
