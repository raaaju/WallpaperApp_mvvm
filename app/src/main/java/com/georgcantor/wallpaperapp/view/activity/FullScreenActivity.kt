package com.georgcantor.wallpaperapp.view.activity

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import com.ablanco.zoomy.Zoomy
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.util.longToast
import com.georgcantor.wallpaperapp.util.showAnimation
import kotlinx.android.synthetic.main.activity_full_screen.*

class FullScreenActivity : Activity() {

    companion object {
        const val FULL_EXTRA = "full_extra"
        const val IS_PORTRAIT = "is_portrait"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)
        requestedOrientation = if (intent.getBooleanExtra(IS_PORTRAIT, true)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        setFullscreen()

        fullAnimationView.showAnimation()

        val zoomyBuilder = Zoomy.Builder(this)
                .target(fullImageView)
        zoomyBuilder.register()

        loadImage(
                intent.getStringExtra(FULL_EXTRA) ?: "",
                resources.getDrawable(R.drawable.splash),
                fullImageView,
                fullAnimationView
        )

        if (!isNetworkAvailable()) longToast(getString(R.string.no_internet))
    }

    private fun setFullscreen() {
        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN

        flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        window.decorView.systemUiVisibility = flags
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    override fun onDestroy() {
        Zoomy.unregister(fullImageView)
        super.onDestroy()
    }

}