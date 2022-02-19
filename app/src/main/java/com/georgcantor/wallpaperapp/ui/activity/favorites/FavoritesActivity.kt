package com.georgcantor.wallpaperapp.ui.activity.favorites

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ActivityFavoritesBinding
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import com.georgcantor.wallpaperapp.ui.activity.BaseActivity
import com.georgcantor.wallpaperapp.ui.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.showDialog
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesActivity : BaseActivity() {

    private val binding by viewBinding(ActivityFavoritesBinding::inflate)
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(R.string.favorites_toolbar)
        }

        viewModel.favorites.observe(this) { list ->
            FavoritesAdapter {
                Gson().fromJson(it.hit, CommonPic::class.java).apply {
                    startActivity<DetailActivity> {
                        putExtra(PIC_EXTRA, CommonPic(url, width, height, tags, imageURL, fullHDURL, id, videoId))
                    }
                }
            }.apply {
                binding.picturesRecycler.adapter = this
                submitList(list)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.favorites_menu, menu)
        val menuItem = menu.findItem(R.id.action_remove_all)

        viewModel.isEmpty.observe(this) {
            menuItem.isVisible = !it
            binding.emptyTv.isVisible = it
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_remove_all) {
            showDialog(
                getString(R.string.delete_all),
                getString(R.string.remove_fav_dialog_message)
            ) { viewModel.deleteAll() }
        }
        return super.onOptionsItemSelected(item)
    }
}