package com.georgcantor.wallpaperapp.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.georgcantor.wallpaperapp.databinding.FragmentGalleryBinding
import com.georgcantor.wallpaperapp.ui.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.ui.fragment.GalleryAdapter
import com.georgcantor.wallpaperapp.ui.fragment.GalleryViewModel
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryActivity : BaseActivity() {

    private val binding by viewBinding(FragmentGalleryBinding::inflate)
    private val viewModel: GalleryViewModel by viewModel()
    private val query by lazy { intent.getStringExtra(PIC_EXTRA) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = query
        }

        val galleryAdapter = GalleryAdapter { pic ->
            startActivity<DetailActivity> { putExtra(PIC_EXTRA, pic) }
        }

        binding.picturesRecycler.apply {
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = galleryAdapter
        }

        lifecycleScope.launch {
            viewModel.getPicListStream(query).collectLatest {
                galleryAdapter.submitData(it)
            }
        }
    }
}