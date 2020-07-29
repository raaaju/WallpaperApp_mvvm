package com.georgcantor.wallpaperapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.loadImage
import kotlinx.android.synthetic.main.item_similar.view.*
import kotlin.properties.Delegates

class SimilarAdapter(private val clickListener: (CommonPic) -> Unit) :
    RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>(), AutoUpdatableAdapter {

    var pictures: List<CommonPic> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder =
        SimilarViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_similar, null))

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
        val picture = pictures.get(position)

        holder.itemView.context.loadImage(
            picture.url ?: "",
            holder.imageView,
            null
        )

        holder.itemView.setOnClickListener {
            picture.let(clickListener)
        }
    }

    override fun getItemCount(): Int = pictures.size

    class SimilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.similarImageView
    }
}