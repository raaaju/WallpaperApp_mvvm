package com.georgcantor.wallpaperapp.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.view.adapter.FavoriteAdapter
import com.georgcantor.wallpaperapp.util.DisposableManager
import com.georgcantor.wallpaperapp.util.getScreenSize
import com.georgcantor.wallpaperapp.util.shortToast
import com.georgcantor.wallpaperapp.util.showDialog
import com.georgcantor.wallpaperapp.viewmodel.FavoriteViewModel
import kotlinx.android.synthetic.main.activity_favorite.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class FavoriteActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: FavoriteAdapter
    private lateinit var viewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setSupportActionBar(favToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = getViewModel { parametersOf() }
        db = DatabaseHelper(this)

        val gridLayoutManager = StaggeredGridLayoutManager(
                getScreenSize(),
                StaggeredGridLayoutManager.VERTICAL
        )
        favRecyclerView.setHasFixedSize(true)
        favRecyclerView.layoutManager = gridLayoutManager

        adapter = FavoriteAdapter(this, db)
        favRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        val disposable = viewModel.getFavorites()
                .subscribe(adapter::setFavList) {
                    shortToast(getString(R.string.something_went_wrong))
                }
        DisposableManager.add(disposable)
        viewModel.isEmptyAnimVisible(emptyAnimationView)
        invalidateOptionsMenu()
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
                showDialog(
                    getString(R.string.remove_fav_dialog_message),
                    ::deleteAll
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAll() {
        viewModel.deleteAll(this)
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}
