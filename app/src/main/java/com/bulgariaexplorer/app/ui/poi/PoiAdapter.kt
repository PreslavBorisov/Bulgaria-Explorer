package com.bulgariaexplorer.app.ui.poi

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.databinding.ItemPoiBinding

class PoiAdapter(
    private val onPoiClick: (PoiResponse) -> Unit
) : ListAdapter<PoiResponse, PoiAdapter.PoiViewHolder>(PoiDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        val binding = ItemPoiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PoiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        val poi = getItem(position)
        if (poi != null) {
            holder.bind(poi)
        }
    }

    inner class PoiViewHolder(
        private val binding: ItemPoiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(poi: PoiResponse) {
            binding.apply {
                tvPoiName.text = poi.title ?: itemView.context.getString(R.string.poi_details_unknown_place)
                tvPoiDescription.text = poi.shortDescription ?: itemView.context.getString(R.string.poi_details_no_description)
                tvPoiCity.text = poi.city ?: itemView.context.getString(R.string.poi_details_unknown_city)
                tvPoiPoints.text = itemView.context.getString(R.string.poi_details_points_format, poi.rewardPoints)

                Log.d("PoiAdapter", "Loading POI ${poi.id}: ${poi.title}, imageUrl: ${poi.imageUrl}")

                // Load image with Coil
                if (!poi.imageUrl.isNullOrEmpty()) {
                    Log.d("PoiAdapter", "Loading image from URL: ${poi.imageUrl}")
                    ivPoiImage.load(poi.imageUrl) {
                        crossfade(true)
                        placeholder(R.mipmap.ic_launcher)
                        error(R.mipmap.ic_launcher)
                        listener(
                            onSuccess = { _, _ ->
                                Log.d("PoiAdapter", "Image loaded successfully for POI ${poi.id}")
                            },
                            onError = { _, result ->
                                Log.e("PoiAdapter", "Image load failed for POI ${poi.id}: ${result.throwable.message}")
                            }
                        )
                    }
                } else {
                    Log.d("PoiAdapter", "No image URL for POI ${poi.id}, using placeholder")
                    ivPoiImage.setImageResource(R.mipmap.ic_launcher)
                }

                root.setOnClickListener {
                    onPoiClick(poi)
                }
            }
        }
    }

    class PoiDiffCallback : DiffUtil.ItemCallback<PoiResponse>() {
        override fun areItemsTheSame(oldItem: PoiResponse, newItem: PoiResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PoiResponse, newItem: PoiResponse): Boolean {
            return oldItem == newItem
        }
    }
}
