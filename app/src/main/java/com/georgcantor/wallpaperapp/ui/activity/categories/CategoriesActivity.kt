package com.georgcantor.wallpaperapp.ui.activity.categories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ActivityCategoriesBinding
import com.georgcantor.wallpaperapp.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoriesActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCategoriesBinding::inflate)
    private val viewModel: CategoriesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        viewModel.categories.observe(this) {
            val categoriesAdapter = CategoriesAdapter(it) {

            }
            binding.recyclerView.apply {
                setHasFixedSize(true)
                binding.recyclerView.adapter = categoriesAdapter
            }
        }
    }
}