package com.georgcantor.wallpaperapp.ui.activity.favorites

import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.georgcantor.wallpaperapp.databinding.ActivityFavoritesBinding
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import com.georgcantor.wallpaperapp.ui.activity.BaseActivity
import com.georgcantor.wallpaperapp.ui.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesActivity : BaseActivity() {

    private val binding by viewBinding(ActivityFavoritesBinding::inflate)
    private val viewModel: FavoritesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel.favorites.observe(this) {
            binding.picturesRecycler.apply {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                adapter = FavoritesAdapter(it) {
                    val hitJson = it.hit
                    val pic = Gson().fromJson(hitJson, CommonPic::class.java)
                    startActivity<DetailActivity> {
                        putExtra(
                            PIC_EXTRA,
                            CommonPic(
                                url = pic.url,
                                width = pic.width,
                                heght = pic.heght,
                                tags = pic.tags,
                                imageURL = pic.imageURL,
                                fullHDURL = pic.fullHDURL,
                                id = pic.id,
                                videoId = pic.videoId
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }
}