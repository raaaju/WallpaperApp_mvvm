package com.georgcantor.wallpaperapp.ui.util

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.georgcantor.wallpaperapp.R

class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var tag: TextView = itemView.findViewById(R.id.tag_item)
}
