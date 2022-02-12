package com.georgcantor.wallpaperapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.FragmentGalleryBinding
import com.georgcantor.wallpaperapp.model.remote.response.LoadableResult
import com.georgcantor.wallpaperapp.ui.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val binding by viewBinding(FragmentGalleryBinding::bind)
    private val viewModel: GalleryViewModel by viewModel()
    private val request by lazy { (findNavController().currentDestination?.label).toString() }
    private val galleryAdapter by lazy {
        GalleryAdapter { pic ->
            requireActivity().startActivity<DetailActivity> { putExtra(PIC_EXTRA, pic) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)= with(binding) {
        super.onViewCreated(view, savedInstanceState)

        galleryAdapter.addLoadStateListener { state ->
            when (val stateRefresh = state.refresh) {
                is LoadState.Error -> {
                    stateViewFlipper.setStateFromResult(LoadableResult.failure<Unit>(stateRefresh.error))
                }
                is LoadState.Loading -> stateViewFlipper.setStateLoading()
                is LoadState.NotLoading -> {
                    stateViewFlipper.setStateData()
                    if (galleryAdapter.itemCount == 0 && state.append.endOfPaginationReached) {
                        stateViewFlipper.setEmptyState()
                    }
                }
            }
        }

        picturesRecycler.apply {
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = galleryAdapter
        }

        getData()
        stateViewFlipper.setEmptyMethod { getData() }
        stateViewFlipper.setRetryMethod { getData() }
    }

    private fun getData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getPicListStream(request).collectLatest {
                galleryAdapter.submitData(it)
            }
        }
    }
}