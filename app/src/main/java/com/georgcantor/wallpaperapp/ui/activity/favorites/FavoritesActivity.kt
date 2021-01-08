package com.georgcantor.wallpaperapp.ui.activity.favorites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.georgcantor.wallpaperapp.databinding.ActivityFavoritesBinding
import com.georgcantor.wallpaperapp.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityFavoritesBinding::inflate)
    private val viewModel: FavoritesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.favorites.observe(this) {
            binding.picturesRecycler.apply {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                adapter = FavoritesAdapter(it) {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }
}