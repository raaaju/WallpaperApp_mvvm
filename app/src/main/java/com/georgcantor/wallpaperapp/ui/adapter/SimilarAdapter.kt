package com.georgcantor.wallpaperapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import kotlinx.android.synthetic.main.similar_item.view.similarImageView

class SimilarAdapter(private val context: Context) : RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>() {

    private val similarList: MutableList<CommonPic>?

    init {
        this.similarList = ArrayList()
    }

    fun setList(similarList: ArrayList<CommonPic>) {
        this.similarList?.addAll(similarList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.similar_item, parent, false)

        return SimilarViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
        Glide.with(context)
            .load(similarList?.get(position)?.url)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = similarList?.size ?: 0

    class SimilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.similarImageView
    }
}