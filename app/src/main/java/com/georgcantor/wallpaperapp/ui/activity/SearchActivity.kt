package com.georgcantor.wallpaperapp.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.georgcantor.wallpaperapp.databinding.ActivitySearchBinding
import com.georgcantor.wallpaperapp.ui.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.ui.fragment.GalleryAdapter
import com.georgcantor.wallpaperapp.ui.fragment.GalleryViewModel
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    private val binding by viewBinding(ActivitySearchBinding::inflate)
    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var galleryAdapter: GalleryAdapter
    private var isFirstRequest = true
    private var isDelayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener { onBackPressed() }

        galleryAdapter = GalleryAdapter { pic ->
            startActivity<DetailActivity> { putExtra(PIC_EXTRA, pic) }
        }

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = galleryAdapter
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (viewModel.q != query) {
                    viewModel.q = query
                    getPictures()
                }
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (viewModel.q != query) {
                    viewModel.q = query
                    getPictures()
                }
                return true
            }
        })

        binding.searchView.onActionViewExpanded()
    }

    private fun getPictures() {
        lifecycleScope.launchWhenStarted {
            if (isFirstRequest) {
                isFirstRequest = false
                viewModel.getPictures().collectLatest { galleryAdapter.submitData(it) }
            } else {
                if (!isDelayed) {
                    isDelayed = true
                    delay(2000)
                    isDelayed = false
                    viewModel.getPictures().collectLatest { galleryAdapter.submitData(it) }
                }
            }
        }
    }
}