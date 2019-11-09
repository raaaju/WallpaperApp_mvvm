package com.georgcantor.wallpaperapp.view

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.ablanco.zoomy.Zoomy
import com.bumptech.glide.Glide
import com.georgcantor.wallpaperapp.R
import kotlinx.android.synthetic.main.activity_full_screen.*

class FullScreenActivity : Activity() {

    companion object {
        const val FULL_EXTRA = "full_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)
        setFullscreen()
        val zoomyBuilder = Zoomy.Builder(this)
                .target(fullImageView)
        zoomyBuilder.register()

        val url = intent.getStringExtra(FULL_EXTRA)
        Glide.with(this)
                .load(url)
                .into(fullImageView)
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

}