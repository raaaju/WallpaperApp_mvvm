package com.georgcantor.wallpaperapp.ui.activity.videos

import android.os.Bundle
import androidx.activity.viewModels
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ActivityVideosBinding
import com.georgcantor.wallpaperapp.ui.activity.BaseActivity
import com.georgcantor.wallpaperapp.util.Constants.ID_EXTRA
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideosActivity : BaseActivity() {

    private val binding by viewBinding(ActivityVideosBinding::inflate)
    private val viewModel: VideosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(R.string.videos)
        }

        viewModel.videos.observe(this) {
            binding.videosRecycler.apply {
                setHasFixedSize(true)
                it?.let {
                    adapter = VideosAdapter(it) {
                        startActivity<VideoActivity> { putExtra(ID_EXTRA, it) }
                    }
                }
            }
        }
    }
}