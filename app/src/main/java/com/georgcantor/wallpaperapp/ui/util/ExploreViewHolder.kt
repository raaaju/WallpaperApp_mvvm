package com.georgcantor.wallpaperapp.ui.util

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.georgcantor.wallpaperapp.R

class ExploreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var category: ImageView
    var categoryName: TextView

    init {
        category = itemView.findViewById(R.id.explore_view)
        categoryName = itemView.findViewById(R.id.categoryTextView)
    }
}
