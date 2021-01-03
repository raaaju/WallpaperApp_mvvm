package com.georgcantor.wallpaperapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.georgcantor.wallpaperapp.databinding.ActivityDetailBinding
import com.georgcantor.wallpaperapp.util.viewBinding

class DetailActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityDetailBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}