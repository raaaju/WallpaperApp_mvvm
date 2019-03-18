package com.georgcantor.wallpaperapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.Pic
import com.georgcantor.wallpaperapp.ui.PicDetailActivity
import com.georgcantor.wallpaperapp.ui.util.WallpViewHolder
import com.squareup.picasso.Picasso
import java.util.*

class WallpAdapter(private val context: Context) : RecyclerView.Adapter<WallpViewHolder>() {

    private val hit: MutableList<Hit>?

    init {
        this.hit = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.wallp_item, null)
        val wallpViewHolder = WallpViewHolder(itemView)

        itemView.setOnClickListener {
            val activity = context as Activity
            val position = wallpViewHolder.adapterPosition
            val intent = Intent(context, PicDetailActivity::class.java)
            intent.putExtra(PicDetailActivity.EXTRA_PIC, hit!![position])
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }

        return wallpViewHolder
    }

    override fun onBindViewHolder(holder: WallpViewHolder, position: Int) {
        val photo = this.hit!![position]
        val layoutParams = holder.discWallp.layoutParams as RelativeLayout.LayoutParams
        val height = photo.previewHeight.toFloat()
        val width = photo.previewWidth.toFloat()
        val ratio = height / width
        layoutParams.height = (layoutParams.width * ratio).toInt()
        holder.discWallp.layoutParams = layoutParams
        holder.discWallp.setRatio(ratio)

        Picasso.with(context)
                .load(photo.webformatURL)
                .placeholder(R.drawable.plh)
                .into(holder.discWallp)
    }

    override fun getItemCount(): Int {
        return hit?.size ?: 0
    }

    fun setPicList(picList: Pic) {
        if (picList.hits != null)
            this.hit!!.addAll(picList.hits)
        notifyDataSetChanged()
    }
}
