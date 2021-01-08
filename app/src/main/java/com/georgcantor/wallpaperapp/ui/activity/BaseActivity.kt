package com.georgcantor.wallpaperapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import com.georgcantor.wallpaperapp.R

abstract class BaseActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }
}