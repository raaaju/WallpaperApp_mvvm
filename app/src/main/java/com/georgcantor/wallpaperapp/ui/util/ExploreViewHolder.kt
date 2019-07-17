package com.georgcantor.wallpaperapp.ui.util

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_explore.view.*

class ExploreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var category: ImageView = itemView.explore_view
    var categoryName: TextView = itemView.expText
}
