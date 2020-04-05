package com.georgcantor.wallpaperapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.util.applySchedulers
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.view.adapter.holder.FavoriteViewHolder
import com.google.gson.Gson
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class FavoriteAdapter(
    private val context: Context,
    private val isNotGrid: Boolean,
    favorites: MutableList<Favorite>,
    private val clickListener: (Favorite) -> Unit,
    private val longClickListener: (Favorite) -> Unit
) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private val favorites: MutableList<Favorite>? = ArrayList()

    init {
        clearPictures()
        this.favorites?.addAll(favorites)
        notifyDataSetChanged()
    }

    private fun clearPictures() {
        val size = favorites?.size
        favorites?.clear()
        size?.let { notifyItemRangeRemoved(0, it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder =
        FavoriteViewHolder(LayoutInflater.from(context).inflate(R.layout.favorite_item, null))

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favorites?.get(position)
        val hitJson = favorite?.hit
        val pic = Gson().fromJson(hitJson, CommonPic::class.java)

        val publishSubject = PublishSubject.create<Int>()
        publishSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .applySchedulers()
            .subscribe {
                favorite?.let(clickListener)
            }

        holder.imageView.setOnClickListener {
            publishSubject.onNext(0)
        }

        holder.imageView.setOnLongClickListener {
            favorites?.get(position)?.let { favorite -> longClickListener(favorite) }
            false
        }

        val layoutParams = holder.imageView.layoutParams as RelativeLayout.LayoutParams
        val height = pic.heght.toFloat()
        val width = pic.width.toFloat()
        val ratio = height.div(width)
        layoutParams.height = (layoutParams.width * ratio).toInt()
        holder.imageView.layoutParams = layoutParams
        holder.imageView.setRatio(ratio)

        context.loadImage(
            if (isNotGrid) pic.fullHDURL ?: "" else pic.url ?: "",
            context.resources.getDrawable(R.drawable.placeholder),
            holder.imageView,
            null
        )
    }

    override fun getItemCount(): Int = favorites?.size ?: 0

}