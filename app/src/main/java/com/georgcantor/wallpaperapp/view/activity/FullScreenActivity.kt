package com.georgcantor.wallpaperapp.view.activity

import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import com.ablanco.zoomy.Zoomy
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.Constants.FULL_EXTRA
import com.georgcantor.wallpaperapp.util.Constants.IS_PORTRAIT
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.util.longToast
import com.georgcantor.wallpaperapp.util.showAnimation
import kotlinx.android.synthetic.main.activity_full_screen.*

class FullScreenActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)
        requestedOrientation = if (intent.getBooleanExtra(IS_PORTRAIT, true)) {
            SCREEN_ORIENTATION_PORTRAIT
        } else {
            SCREEN_ORIENTATION_LANDSCAPE
        }

        fullAnimationView.showAnimation()

        val zoomyBuilder = Zoomy.Builder(this).target(fullImageView)
        zoomyBuilder.register()

        loadImage(
            intent.getStringExtra(FULL_EXTRA) ?: "",
            fullImageView,
            fullAnimationView
        )

        if (!isNetworkAvailable()) longToast(getString(R.string.no_internet))
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