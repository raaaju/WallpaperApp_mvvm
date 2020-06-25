package com.georgcantor.wallpaperapp.view.fragment.videos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.openFragment
import com.georgcantor.wallpaperapp.view.fragment.videos.full_screen.FullScreenFragment
import kotlinx.android.synthetic.main.fragment_videos.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import org.koin.ext.getScopeName

class VideosFragment : Fragment() {

    private lateinit var viewModel: VideosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_videos, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            getVideos("PL49XWbU5C8KiFE_8NHAvExqfC-kNciIMu")

            videos.observe(viewLifecycleOwner, Observer {
                videos_recycler.setHasFixedSize(true)
                videos_recycler.adapter = VideosAdapter(it) {
                    (requireActivity() as AppCompatActivity).openFragment(
                        FullScreenFragment.create(it),
                        FullScreenFragment().getScopeName().value
                    )
                }
            })
        }
    }
}