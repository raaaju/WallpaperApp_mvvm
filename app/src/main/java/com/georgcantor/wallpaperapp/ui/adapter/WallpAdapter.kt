package com.georgcantor.wallpaperapp.ui.adapter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.ui.fragment.DetailFragment
import com.georgcantor.wallpaperapp.ui.util.WallpViewHolder
import com.squareup.picasso.Picasso
import java.util.*

class WallpAdapter(private val context: Context,
                   private val fragmentManager: FragmentManager) : RecyclerView.Adapter<WallpViewHolder>() {

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
        val viewHolder = WallpViewHolder(itemView)

        itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val bundle = Bundle()
            bundle.putParcelable(DetailFragment.EXTRA_PIC, hit?.get(position))
            val fragment = DetailFragment()
            fragment.arguments = bundle

            openFragment(fragment)
        }

        return viewHolder
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

    private fun openFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
