package com.georgcantor.wallpaperapp.ui.activity.videos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.remote.response.videos.Item
import com.georgcantor.wallpaperapp.util.loadImage

class VideosAdapter(
    private val videos: List<Item>,
    private val clickListener: (String?) -> Unit
) : RecyclerView.Adapter<VideosAdapter.VideosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VideosViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
    )

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val video = videos[position]
        with(holder) {
            video.snippet?.thumbnails?.standard?.url?.let {
                itemView.context.loadImage(
                    it,
                    image,
                    null,
                    R.color.gray
                )
            }
            itemView.setOnClickListener { clickListener(video.snippet?.resourceId?.videoId) }
        }
    }

    override fun getItemCount() = videos.size

    class VideosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.video_image)
    }
}