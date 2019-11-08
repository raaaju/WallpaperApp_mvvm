package com.georgcantor.wallpaperapp.view.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_item.view.*

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var categoryImage: ImageView = itemView.categoryImageView
    var categoryName: TextView = itemView.categoryTitle
}
