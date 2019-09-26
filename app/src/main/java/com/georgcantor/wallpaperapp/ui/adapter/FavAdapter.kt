package com.georgcantor.wallpaperapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.model.local.db.Favorite
import com.georgcantor.wallpaperapp.ui.adapter.holder.FavoriteViewHolder
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.util.ArrayList

class FavAdapter(private val context: Context) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private val favorites: MutableList<Favorite>?

    init {
        this.favorites = ArrayList()
    }

    fun setFavList(strings: MutableList<Favorite>) {
        this.favorites?.addAll(strings)
        notifyDataSetChanged()
    }

    fun clearFavList() {
        val size = favorites?.size
        favorites?.clear()
        size?.let { notifyItemRangeRemoved(0, it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.favorite_item, null)
        val wallpViewHolder = FavoriteViewHolder(itemView)

        return wallpViewHolder
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        this.favorites.let {
            val favorite = favorites?.get(position)
            val hitJson = favorite?.hit

            val gson = Gson()
            val pic = gson.fromJson(hitJson, CommonPic::class.java)

            val layoutParams = holder.imageView.layoutParams as RelativeLayout.LayoutParams
            val height = pic.heght.toFloat()
            val width = pic.width.toFloat()
            val ratio = height.div(width)
            layoutParams.height = (layoutParams.width * ratio).toInt()
            holder.imageView.layoutParams = layoutParams
            holder.imageView.setRatio(ratio)

            Picasso.with(context)
                .load(pic.url)
                .placeholder(R.drawable.plh)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = favorites?.size ?: 0

}