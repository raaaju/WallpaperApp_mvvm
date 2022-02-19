package com.georgcantor.wallpaperapp.ui.activity.categories

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.georgcantor.wallpaperapp.databinding.ActivityCategoriesBinding
import com.georgcantor.wallpaperapp.ui.activity.BaseActivity
import com.georgcantor.wallpaperapp.ui.activity.GalleryActivity
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesActivity : BaseActivity() {

    private val binding by viewBinding(ActivityCategoriesBinding::inflate)
    private val viewModel: CategoriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel.categories.observe(this) {
            binding.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(this@CategoriesActivity, 2)
                adapter = CategoriesAdapter(it) {
                    startActivity<GalleryActivity> { putExtra(PIC_EXTRA, it.categoryName) }
                }
            }
        }

        viewModel.progressIsVisible.observe(this) {
            binding.progressBar.isVisible = it
        }
    }
}