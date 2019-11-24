package com.georgcantor.wallpaperapp.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.util.DisposableManager
import com.georgcantor.wallpaperapp.util.getScreenSize
import com.georgcantor.wallpaperapp.util.shortToast
import com.georgcantor.wallpaperapp.util.showDialog
import com.georgcantor.wallpaperapp.view.adapter.FavoriteAdapter
import com.georgcantor.wallpaperapp.viewmodel.FavoriteViewModel
import kotlinx.android.synthetic.main.activity_favorite.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class FavoriteActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setSupportActionBar(favToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = getViewModel { parametersOf(this) }

        val gridLayoutManager = StaggeredGridLayoutManager(
                getScreenSize(),
                StaggeredGridLayoutManager.VERTICAL
        )
        favRecyclerView.setHasFixedSize(true)
        favRecyclerView.layoutManager = gridLayoutManager
    }

    override fun onResume() {
        super.onResume()
        val disposable = viewModel.getFavorites()
                .subscribe({
                    favRecyclerView.adapter = FavoriteAdapter(this, it) { fav: Favorite ->
                        showDialog(getString(R.string.del_from_fav_dialog)) {
                            viewModel.deleteByUrl(fav.url)
                            recreate()
                        }
                    }
                }, {
                    shortToast(getString(R.string.something_went_wrong))
                })
        DisposableManager.add(disposable)
        viewModel.isEmptyAnimVisible(emptyAnimationView)
        invalidateOptionsMenu()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    @SuppressLint("CheckResult")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        val menuItem = menu.findItem(R.id.action_remove_all)

        viewModel.dbIsNotEmpty()
                .subscribe({
                    menuItem.isVisible = it
                }, {
                })

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

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

    private fun deleteAll() {
        viewModel.deleteAll()
    }

}
