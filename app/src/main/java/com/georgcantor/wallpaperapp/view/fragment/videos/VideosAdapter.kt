package com.georgcantor.wallpaperapp.view.fragment.videos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.loadImage
import kotlinx.android.synthetic.main.item_video.view.*

class VideosAdapter(
    private val pictures: List<CommonPic>,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<VideosAdapter.VideosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VideosViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
    )

    override fun getItemCount() = pictures.size

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val picture = pictures[position]

        with(holder) {
            itemView.context.loadImage(picture.url?:"", imageView, null)

            itemView.setOnClickListener { clickListener(picture.videoId ?: "") }
        }
    }

    class VideosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = itemView.video_image
    }
}