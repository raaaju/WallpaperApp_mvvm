package com.georgcantor.wallpaperapp.ui.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.ui.util.DynamicHeightImageView
import kotlinx.android.synthetic.main.wallp_item.view.*

class WallpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imageView: DynamicHeightImageView = itemView.wallpView
}
