package com.georgcantor.wallpaperapp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.local.db.Favorite
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
                emptyAnimationView.visibility = View.GONE
            } else {
                emptyAnimationView.visibility = View.VISIBLE
                emptyAnimationView.playAnimation()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        val menuItem = menu.findItem(R.id.action_remove_all)
        db?.let {
            (it.historyCount > 0)
        }?.let(menuItem::setVisible)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
            }
            R.id.action_remove_all -> {
                showDeleteDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.remove_fav_dialog_message)
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            db?.let {
                if (it.historyCount > 0) {
                    it.deleteAll()
                    this.recreate()
                }
            }
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
        builder.setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
        builder.create().show()
    }

}
