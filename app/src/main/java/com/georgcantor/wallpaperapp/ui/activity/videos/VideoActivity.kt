package com.georgcantor.wallpaperapp.ui.activity.videos

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import com.georgcantor.wallpaperapp.databinding.ActivityVideoBinding
import com.georgcantor.wallpaperapp.ui.activity.BaseActivity
import com.georgcantor.wallpaperapp.util.Constants.ID_EXTRA
import com.georgcantor.wallpaperapp.util.viewBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoActivity : BaseActivity() {

    private val binding by viewBinding(ActivityVideoBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        with(binding.playerView) {
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(intent.getStringExtra(ID_EXTRA).orEmpty(), 0F)
                }
            })

            getPlayerUiController().setFullScreenButtonClickListener {
                if (isFullScreen()) setPortraitMode() else setLandscapeMode()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandscapeMode()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitMode()
        }
    }

    override fun onDestroy() {
        binding.playerView.release()
        super.onDestroy()
    }

    private fun setPortraitMode() {
        binding.playerView.exitFullScreen()
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        supportActionBar?.show()
    }

    private fun setLandscapeMode() {
        binding.playerView.enterFullScreen()
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
    }
}