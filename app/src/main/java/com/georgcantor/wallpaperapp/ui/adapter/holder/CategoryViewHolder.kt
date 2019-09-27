package com.georgcantor.wallpaperapp.ui.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_explore.view.*

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var categoryImage: ImageView = itemView.categoryImageView
    var categoryName: TextView = itemView.categoryTitle
}
