package com.georgcantor.wallpaperapp.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.CommonPic
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.local.db.Favorite
import com.georgcantor.wallpaperapp.ui.DetailsActivity
import com.georgcantor.wallpaperapp.ui.adapter.holder.FavoriteViewHolder
import com.georgcantor.wallpaperapp.util.longToast
import com.georgcantor.wallpaperapp.util.showDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class FavoriteAdapter(private val context: Context) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private val favorites: MutableList<Favorite>?
    private val activity = context as Activity
    private val db = DatabaseHelper(context)

    init {
        this.favorites = ArrayList()
    }

    fun setFavList(strings: MutableList<Favorite>) {
        clearPicList()
        this.favorites?.addAll(strings)
        notifyDataSetChanged()
    }

    private fun clearPicList() {
        val size = favorites?.size
        favorites?.clear()
        size?.let { notifyItemRangeRemoved(0, it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.favorite_item, null)

        return FavoriteViewHolder(itemView)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favorites?.get(position)
        val hitJson = favorite?.hit

        val gson = Gson()
        val pic = gson.fromJson(hitJson, CommonPic::class.java)

        val publishSubject = PublishSubject.create<Int>()
        publishSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(context, DetailsActivity::class.java)
                try {
                    intent.putExtra(
                        DetailsActivity.EXTRA_PIC,
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
                    context.longToast(context.getString(R.string.something_went_wrong))
                }
                context.startActivity(intent)
                activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
            }

        holder.imageView.setOnClickListener {
            publishSubject.onNext(0)
        }

        holder.imageView.setOnLongClickListener {
            val photo = favorites?.get(position)
            val url = photo?.imageUrl

            context.showDialog(context.getString(R.string.del_from_fav_dialog)) {
                deleteFromFavorites(url)
            }
            false
        }

        val layoutParams = holder.imageView.layoutParams as RelativeLayout.LayoutParams
        val height = pic.heght.toFloat()
        val width = pic.width.toFloat()
        val ratio = height.div(width)
        layoutParams.height = (layoutParams.width * ratio).toInt()
        holder.imageView.layoutParams = layoutParams
        holder.imageView.setRatio(ratio)

        Glide.with(context)
            .load(pic.url)
            .placeholder(R.drawable.plh)
            .thumbnail(0.1f)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = favorites?.size ?: 0

    private fun deleteFromFavorites(url: String?) {
        if (url != null) {
            db.deleteFromFavorites(url)
        }
        activity.recreate()
    }

}