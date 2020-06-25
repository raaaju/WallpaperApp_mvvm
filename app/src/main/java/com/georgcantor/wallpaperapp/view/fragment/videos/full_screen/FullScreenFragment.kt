package com.georgcantor.wallpaperapp.view.fragment.videos.full_screen

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.gone
import com.georgcantor.wallpaperapp.util.visible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_full_screen.*

class FullScreenFragment : Fragment() {

    companion object {
        private const val ARG_ID = "video_id"

        fun create(id: String): FullScreenFragment {
            return FullScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, id)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_full_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
        requireActivity().navigation.gone()
        requireActivity().appBar.gone()

        with(player_full_view) {
            enterFullScreen()
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(arguments?.get(ARG_ID) as String, 0f)
                }
            })

            removeFullScreenListener(object : AbstractYouTubePlayerListener(),
                YouTubePlayerFullScreenListener {
                override fun onYouTubePlayerEnterFullScreen() {
                }

                override fun onYouTubePlayerExitFullScreen() {
                    requireActivity().onBackPressed()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        requireActivity().navigation.visible()
        requireActivity().appBar.visible()
    }
}