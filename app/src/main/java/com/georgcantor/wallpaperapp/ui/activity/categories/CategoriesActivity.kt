package com.georgcantor.wallpaperapp.ui.activity.categories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.georgcantor.wallpaperapp.databinding.ActivityCategoriesBinding
import com.georgcantor.wallpaperapp.ui.activity.GalleryActivity
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.setVisibility
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoriesActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCategoriesBinding::inflate)
    private val viewModel: CategoriesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
            binding.progressBar.setVisibility(it)
        }
    }
}