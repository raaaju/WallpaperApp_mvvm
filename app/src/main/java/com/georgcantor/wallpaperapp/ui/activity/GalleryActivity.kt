package com.georgcantor.wallpaperapp.ui.activity

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.FragmentGalleryBinding
import com.georgcantor.wallpaperapp.model.remote.response.LoadableResult
import com.georgcantor.wallpaperapp.ui.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.ui.fragment.GalleryAdapter
import com.georgcantor.wallpaperapp.ui.fragment.GalleryViewModel
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.NetworkUtils.getNetworkLiveData
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryActivity : BaseActivity() {

    private val binding by viewBinding(FragmentGalleryBinding::inflate)
    private val viewModel: GalleryViewModel by viewModel()
    private val query by lazy { intent.getStringExtra(PIC_EXTRA).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) = with(binding) {
        super.onCreate(savedInstanceState)
        setContentView(root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = query
        }

        val adapter = GalleryAdapter { pic ->
            startActivity<DetailActivity> { putExtra(PIC_EXTRA, pic) }
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
                        stateViewFlipper.setEmptyStateWithTitles(
                            getString(R.string.something_went_wrong),
                            getString(R.string.something_went_wrong),
                            getString(R.string.something_went_wrong)
                        )
                    }
                }
            }
        }

        picturesRecycler.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.getPicListStream(query).collectLatest {
                adapter.submitData(it)
            }
        }

        getNetworkLiveData(applicationContext).observe(this@GalleryActivity) {
            noInternetWarning.isVisible = !it
        }
        stateViewFlipper.setEmptyMethod {  }
        stateViewFlipper.setRetryMethod {  }
    }
}