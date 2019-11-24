package com.georgcantor.wallpaperapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.loadImage
import kotlinx.android.synthetic.main.similar_item.view.*

class SimilarAdapter(
        private val context: Context,
        pictures: MutableList<CommonPic>,
        private val clickListener: (CommonPic) -> Unit
) : RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>() {

    private val pictures: MutableList<CommonPic>? = ArrayList()

    init {
        this.pictures?.addAll(pictures)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder =
            SimilarViewHolder(LayoutInflater.from(context).inflate(R.layout.similar_item, null))

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
        val picture = pictures?.get(position)

        context.loadImage(
                picture?.url ?: "",
                context.resources.getDrawable(R.drawable.placeholder),
                holder.imageView,
                null
        )

        holder.itemView.setOnClickListener {
            picture?.let(clickListener)
        }
    }

    override fun getItemCount(): Int = pictures?.size ?: 0

    class SimilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.similarImageView
    }

}