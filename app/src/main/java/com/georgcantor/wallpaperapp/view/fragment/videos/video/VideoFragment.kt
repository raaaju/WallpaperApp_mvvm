package com.georgcantor.wallpaperapp.view.fragment.videos.video

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_VISIBLE
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.Constants.ARG_PLAYLIST_ID
import com.georgcantor.wallpaperapp.util.gone
import com.georgcantor.wallpaperapp.util.visible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_video.*

class VideoFragment : Fragment() {

    companion object {
        fun create(id: String): VideoFragment {
            return VideoFragment().apply {
                arguments = Bundle().apply { putString(ARG_PLAYLIST_ID, id) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().navigation.gone()
        requireActivity().appBar.gone()

        with(player_view) {
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(arguments?.get(ARG_PLAYLIST_ID) as String, 0f)

                }
            })

            getPlayerUiController()
                .setFullScreenButtonClickListener(View.OnClickListener {
                    if (isFullScreen()) {
                        exitFullScreen()
                        activity?.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
                        activity?.window?.decorView?.systemUiVisibility = SYSTEM_UI_FLAG_VISIBLE
                        activity?.actionBar?.show()
                    } else {
                        enterFullScreen()
                        activity?.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
                        activity?.window?.decorView?.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
                        activity?.actionBar?.hide()
                    }
                })
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        activity?.window?.decorView?.systemUiVisibility = SYSTEM_UI_FLAG_VISIBLE
        requireActivity().navigation.visible()
        requireActivity().appBar.visible()
    }
}