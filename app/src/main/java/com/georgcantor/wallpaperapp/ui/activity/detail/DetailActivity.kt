package com.georgcantor.wallpaperapp.ui.activity.detail

import android.os.Bundle
import com.ablanco.zoomy.Zoomy
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ActivityDetailBinding
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import com.georgcantor.wallpaperapp.ui.activity.BaseActivity
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.util.share
import com.georgcantor.wallpaperapp.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : BaseActivity() {

    private val binding by viewBinding(ActivityDetailBinding::inflate)
    private val viewModel: DetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pic = intent.getParcelableExtra(PIC_EXTRA) as CommonPic?

        loadImage(pic?.imageURL, binding.image, binding.progressBar, R.color.black)

        Zoomy.Builder(this).target(binding.image).apply { register() }

        viewModel.isFavorite(pic?.url)

        viewModel.isFavorite.observe(this) {
            binding.bottomAppBar.menu.findItem(R.id.action_add_to_fav).setIcon(
                if (it) R.drawable.ic_star_red_24dp else R.drawable.ic_star_border
            )
        }

        binding.bottomAppBar.setNavigationOnClickListener { onBackPressed() }
        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_share -> share(pic?.imageURL)
                R.id.action_download -> {}
                R.id.action_add_to_fav -> viewModel.addOrRemoveFromFavorites(pic)
            }
            true
        }
    }

    override fun onDestroy() {
        Zoomy.unregister(binding.image)
        super.onDestroy()
    }
}