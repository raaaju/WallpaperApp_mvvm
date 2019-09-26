package com.georgcantor.wallpaperapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.local.db.Favorite
import com.georgcantor.wallpaperapp.ui.PicDetailActivity
import com.georgcantor.wallpaperapp.ui.adapter.holder.FavoriteViewHolder
import com.georgcantor.wallpaperapp.ui.util.longToast
import com.georgcantor.wallpaperapp.ui.util.showDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.util.ArrayList

class FavoriteAdapter(private val context: Context) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private val favorites: MutableList<Favorite>?
    private val activity = context as Activity
    private val db = DatabaseHelper(context)

    init {
        this.favorites = ArrayList()
    }

    fun setFavList(strings: MutableList<Favorite>) {
        this.favorites?.addAll(strings)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.favorite_item, null)

        return FavoriteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favorites?.get(position)
        val hitJson = favorite?.hit

        val gson = Gson()
        val pic = gson.fromJson(hitJson, CommonPic::class.java)

        holder.imageView.setOnClickListener {
            val intent = Intent(context, PicDetailActivity::class.java)
            try {
                intent.putExtra(
                    PicDetailActivity.EXTRA_PIC,
                    CommonPic(
                        url = pic.url,
                        width = pic.width,
                        heght = pic.heght,
                        likes = pic.likes,
                        favorites = pic.favorites,
                        tags = pic.tags,
                        downloads = pic.downloads,
                        imageURL = pic.imageURL,
                        fullHDURL = pic.fullHDURL,
                        user = pic.user,
                        userImageURL = pic.userImageURL
                    )
                )
            } catch (e: ArrayIndexOutOfBoundsException) {
                context.longToast(context.resources.getString(R.string.something_went_wrong))
            }
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }

        holder.imageView.setOnLongClickListener {
            val photo = favorites?.get(position)
            val url = photo?.imageUrl

            context.showDialog(context.resources.getString(R.string.del_from_fav_dialog)) {
                deleteFromFavorites(url)
            }
            false
        }

        this.favorites.let {
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

    private fun deleteFromFavorites(url: String?) {
        if (url != null) {
            db.deleteFromFavorites(url)
        }
        activity.recreate()
    }

}