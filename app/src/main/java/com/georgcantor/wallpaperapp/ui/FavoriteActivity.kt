package com.georgcantor.wallpaperapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.ui.adapter.FavoriteAdapter
import com.georgcantor.wallpaperapp.viewmodel.FavoriteViewModel
import kotlinx.android.synthetic.main.activity_favorite.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class FavoriteActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: FavoriteAdapter
    private lateinit var viewModel: FavoriteViewModel

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setSupportActionBar(favToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.favorites)

        viewModel = getViewModel { parametersOf() }
        db = DatabaseHelper(this)

        viewModel.getFavorites().subscribe({
            adapter = FavoriteAdapter(this, R.layout.favorite_list_row, it)
            favGridView.adapter = adapter
        }, {
            Toast.makeText(this, resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT).show()
        })

        toggleEmptyHistory()
    }

    private fun toggleEmptyHistory() {
        if (db.historyCount > 0) {
            emptyAnimationView.visibility = View.GONE
        } else {
            emptyAnimationView.visibility = View.VISIBLE
            emptyAnimationView.playAnimation()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        val menuItem = menu.findItem(R.id.action_remove_all)
        (db.historyCount > 0).let(menuItem::setVisible)

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
            if (db.historyCount > 0) {
                db.deleteAll()
                this.recreate()
            }
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
        builder.setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
        builder.create().show()
    }

}
