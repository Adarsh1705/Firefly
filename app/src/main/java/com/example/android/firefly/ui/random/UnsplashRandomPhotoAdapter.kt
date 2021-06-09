package com.example.android.firefly.ui.random

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.android.firefly.R
import com.example.android.firefly.data.UnsplashPhoto
import com.example.android.firefly.databinding.ItemUnsplashPhotoBinding

class UnsplashRandomPhotoAdapter(private val listener:OnItemClickistener):
    PagingDataAdapter<UnsplashPhoto, UnsplashRandomPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PhotoViewHolder(
            ItemUnsplashPhotoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)
        if(currentItem!=null){
            holder.bind(currentItem)
        }
    }

    inner class PhotoViewHolder(private val b: ItemUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }
        fun bind(photo:UnsplashPhoto){
        b.apply {
            Glide.with(itemView)
                .load(photo.urls.regular)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.feather)
                .into(recyclerImage)
        }
    }
    }

    interface OnItemClickistener{
        fun onItemClick(photo:UnsplashPhoto)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem == newItem      //Since its a data class is acts like equals method
        }
    }
}



