package com.bulgariaexplorer.app.ui.poi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.databinding.ItemPhotoGalleryBinding

class PhotoGalleryAdapter(
    private val photos: List<String>,
    private val onPhotoClick: (String) -> Unit
) : RecyclerView.Adapter<PhotoGalleryAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(
        private val binding: ItemPhotoGalleryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            binding.ivPhoto.load(url) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher)
                error(R.mipmap.ic_launcher)
            }
            binding.root.setOnClickListener { onPhotoClick(url) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoGalleryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount() = photos.size
}
