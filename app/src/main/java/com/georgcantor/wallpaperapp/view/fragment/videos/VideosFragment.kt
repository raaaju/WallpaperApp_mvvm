package com.georgcantor.wallpaperapp.view.fragment.videos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import org.koin.ext.getScopeName

class VideosFragment : Fragment() {

    companion object {
        fun create(id: String): VideosFragment {
            return VideosFragment().apply {
                arguments = Bundle().apply { putString(ARG_PLAYLIST_ID, id) }
            }
        }
    }

    private lateinit var viewModel: VideosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_videos, container, false)

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