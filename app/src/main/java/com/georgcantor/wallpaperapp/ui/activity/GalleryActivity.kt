package com.georgcantor.wallpaperapp.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.georgcantor.wallpaperapp.databinding.FragmentGalleryBinding
import com.georgcantor.wallpaperapp.model.remote.response.LoadableResult
import com.georgcantor.wallpaperapp.ui.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.ui.fragment.GalleryAdapter
import com.georgcantor.wallpaperapp.ui.fragment.GalleryViewModel
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.NetworkUtils.getNetworkLiveData
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GalleryActivity : BaseActivity() {

    private val binding by viewBinding(FragmentGalleryBinding::inflate)
    private val viewModel: GalleryViewModel by viewModels()
    private val query by lazy { intent.getStringExtra(PIC_EXTRA).orEmpty() }
    private val adapter by lazy {
        GalleryAdapter { startActivity<DetailActivity> { putExtra(PIC_EXTRA, it) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) = with(binding) {
        super.onCreate(savedInstanceState)
        setContentView(root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = query
        }

        adapter.addLoadStateListener { state ->
            when (val stateRefresh = state.refresh) {
                is LoadState.Error -> {
                    stateViewFlipper.setStateFromResult(LoadableResult.failure<Unit>(stateRefresh.error))
                }
                is LoadState.Loading -> stateViewFlipper.setStateLoading()
                is LoadState.NotLoading -> {
                    stateViewFlipper.setStateData()
                    if (adapter.itemCount == 0 && state.append.endOfPaginationReached) {
                        stateViewFlipper.setEmptyState()
                    }
                }
            }
        }

        picturesRecycler.adapter = adapter
        getData()
        getNetworkLiveData(applicationContext).observe(this@GalleryActivity) {
            noInternetWarning.isVisible = !it
        }
        stateViewFlipper.setEmptyMethod { getData() }
        stateViewFlipper.setRetryMethod { getData() }
    }

    private fun getData() {
        lifecycleScope.launchWhenStarted {
            viewModel.getPicListStream(query).collectLatest {
                adapter.submitData(it)
            }
        }
    }
}