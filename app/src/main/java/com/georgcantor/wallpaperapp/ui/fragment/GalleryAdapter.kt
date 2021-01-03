package com.georgcantor.wallpaperapp.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.databinding.ItemPictureBinding
import com.georgcantor.wallpaperapp.model.response.CommonPic
import com.georgcantor.wallpaperapp.util.loadImage

class GalleryAdapter(
    private val clickListener: (CommonPic?) -> Unit
) : PagingDataAdapter<CommonPic, RecyclerView.ViewHolder>(picComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GalleryViewHolder(
        ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val picture = getItem(position)
        (holder as GalleryViewHolder).apply {
            itemView.context.loadImage(picture?.url, binding.image)
            itemView.setOnClickListener { clickListener(picture) }
        }
    }

    class GalleryViewHolder(val binding: ItemPictureBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val picComparator = object : DiffUtil.ItemCallback<CommonPic>() {
            override fun areItemsTheSame(oldItem: CommonPic, newItem: CommonPic): Boolean {
                return (oldItem.id == newItem.id) && (oldItem.url == newItem.url)
            }

            override fun areContentsTheSame(oldItem: CommonPic, newItem: CommonPic) = oldItem == newItem
        }
    }
}