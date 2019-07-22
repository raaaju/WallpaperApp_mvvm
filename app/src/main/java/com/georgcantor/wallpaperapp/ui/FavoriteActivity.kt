package com.georgcantor.wallpaperapp.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.db.Favorite
import com.georgcantor.wallpaperapp.ui.adapter.FavoriteAdapter
import kotlinx.android.synthetic.main.activity_favorite.*
import java.util.*

class FavoriteActivity : AppCompatActivity() {

    private var list: ArrayList<Favorite>? = null
    private var db: DatabaseHelper? = null
    private var adapter: FavoriteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setSupportActionBar(favToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.favorites)

        db = DatabaseHelper(this)
        list = ArrayList()
        db?.allFavorites?.let { list?.addAll(it) }

        list?.let { adapter = FavoriteAdapter(this, R.layout.favorite_list_row, it) }
        favGridView.adapter = adapter

        toggleEmptyHistory()
    }

    private fun toggleEmptyHistory() {
        db?.let {
            if (it.historyCount > 0) {
                emptyTextView.visibility = View.GONE
            } else {
                emptyTextView.visibility = View.VISIBLE
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
