package com.georgcantor.wallpaperapp.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.databinding.ItemPictureBinding
import com.georgcantor.wallpaperapp.model.response.pixabay.Hit
import com.georgcantor.wallpaperapp.util.loadImage

class GalleryAdapter : PagingDataAdapter<Hit, RecyclerView.ViewHolder>(picComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GalleryViewHolder(
        ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val picture = getItem(position)
        (holder as GalleryViewHolder).apply {
            itemView.context.loadImage(picture?.webformatURL, binding.image)
        }
    }

    class GalleryViewHolder(val binding: ItemPictureBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val picComparator = object : DiffUtil.ItemCallback<Hit>() {
            override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
                return (oldItem.id == newItem.id) && (oldItem.user == newItem.user)
            }

            override fun areContentsTheSame(oldItem: Hit, newItem: Hit) = oldItem == newItem
        }
    }
}