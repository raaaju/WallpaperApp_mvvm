package com.georgcantor.wallpaperapp.view.fragment.videos

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.Constants.ARG_PLAYLIST_ID
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import com.georgcantor.wallpaperapp.util.longToast
import com.georgcantor.wallpaperapp.util.openFragment
import com.georgcantor.wallpaperapp.util.showAnimation
import com.georgcantor.wallpaperapp.view.fragment.videos.video.VideoFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_videos.*
import org.koin.android.ext.android.inject
import org.koin.ext.getScopeName

class VideosFragment : Fragment(R.layout.fragment_videos) {

    companion object {
        fun create(id: String): VideosFragment {
            return VideosFragment().apply {
                arguments = Bundle().apply { putString(ARG_PLAYLIST_ID, id) }
            }
        }
    }

    private val viewModel by inject<VideosViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!requireContext().isNetworkAvailable()) {
            noInternetAnimationView.showAnimation()
            context?.longToast(getString(R.string.no_internet))
        }

        with(viewModel) {
            getVideos(arguments?.get(ARG_PLAYLIST_ID) as String)

            videos.observe(viewLifecycleOwner, Observer {
                videos_recycler.setHasFixedSize(true)
                videos_recycler.adapter = VideosAdapter(it) {
                    activity?.appBar?.setExpanded(false)
                    (requireActivity() as AppCompatActivity).openFragment(
                        VideoFragment.create(it),
                        VideoFragment().getScopeName().value
                    )
                }
            })
        }
    }
}