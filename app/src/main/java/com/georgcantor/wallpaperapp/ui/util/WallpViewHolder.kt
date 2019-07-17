package com.georgcantor.wallpaperapp.ui.util

import android.support.v7.widget.RecyclerView
import android.view.View

import com.georgcantor.wallpaperapp.R

class WallpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imageView: DynamicHeightImageView = itemView.findViewById(R.id.wallpView)
}
