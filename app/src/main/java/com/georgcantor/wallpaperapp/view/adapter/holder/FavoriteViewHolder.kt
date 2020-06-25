package com.georgcantor.wallpaperapp.view.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.util.DynamicHeightImageView
import kotlinx.android.synthetic.main.item_favorite.view.favoriteImageView

class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imageView: DynamicHeightImageView = itemView.favoriteImageView
}