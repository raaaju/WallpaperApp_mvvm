package com.georgcantor.wallpaperapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.ui.PicDetailActivity
import com.georgcantor.wallpaperapp.ui.adapter.holder.WallpViewHolder
import com.georgcantor.wallpaperapp.ui.util.longToast
import com.squareup.picasso.Picasso
import java.util.*

class WallpAdapter(private val context: Context) : RecyclerView.Adapter<WallpViewHolder>() {

    private val hit: MutableList<Hit>?

    init {
        this.hit = ArrayList()
    }

    fun setPicList(hits: MutableList<Hit>) {
        this.hit?.addAll(hits)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.wallp_item, null)
        val wallpViewHolder = WallpViewHolder(itemView)

        itemView.setOnClickListener {
            val activity = context as Activity
            val position = wallpViewHolder.adapterPosition
            val intent = Intent(context, PicDetailActivity::class.java)
            try {
                intent.putExtra(PicDetailActivity.EXTRA_PIC, hit?.get(position))
            } catch (e: ArrayIndexOutOfBoundsException) {
                context.longToast(context.resources.getString(R.string.something_went_wrong))
            }
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }

        return wallpViewHolder
    }

    override fun onBindViewHolder(holder: WallpViewHolder, position: Int) {
        this.hit?.let {
            val photo = it[position]
            val layoutParams = holder.imageView.layoutParams as RelativeLayout.LayoutParams
            val height = photo.previewHeight.toFloat()
            val width = photo.previewWidth.toFloat()
            val ratio = height / width
            layoutParams.height = (layoutParams.width * ratio).toInt()
            holder.imageView.layoutParams = layoutParams
            holder.imageView.setRatio(ratio)

            Picasso.with(context)
                    .load(photo.webformatURL)
                    .placeholder(R.drawable.plh)
                    .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = hit?.size ?: 0
}
