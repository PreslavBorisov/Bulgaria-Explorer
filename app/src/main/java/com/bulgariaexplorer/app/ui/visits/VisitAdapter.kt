package com.bulgariaexplorer.app.ui.visits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.remote.dto.VisitResponse
import com.bulgariaexplorer.app.databinding.ItemVisitBinding

class VisitAdapter(
    private val onVisitClick: (VisitResponse) -> Unit
) : ListAdapter<VisitResponse, VisitAdapter.VisitViewHolder>(VisitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val binding = ItemVisitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VisitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VisitViewHolder(
        private val binding: ItemVisitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(visit: VisitResponse) {
            binding.apply {
                tvPoiName.text = visit.poiTitle ?: itemView.context.getString(R.string.visit_unknown_place)
                tvPoints.text = itemView.context.getString(R.string.visit_points_format, visit.totalAwardedPoints)
                tvDate.text = formatDate(visit.visitedAt)

                if (!visit.poiImageUrl.isNullOrEmpty()) {
                    ivPoiImage.load(visit.poiImageUrl) {
                        crossfade(true)
                        placeholder(R.mipmap.ic_launcher)
                        error(R.mipmap.ic_launcher)
                    }
                } else if (!visit.photoUrl.isNullOrEmpty()) {
                    ivPoiImage.load(visit.photoUrl) {
                        crossfade(true)
                        placeholder(R.mipmap.ic_launcher)
                        error(R.mipmap.ic_launcher)
                    }
                } else {
                    ivPoiImage.setImageResource(R.mipmap.ic_launcher)
                }

                root.setOnClickListener {
                    onVisitClick(visit)
                }
            }
        }

        private fun formatDate(dateStr: String): String {
            return try {
                val parts = dateStr.split("T")
                if (parts.size >= 2) {
                    val datePart = parts[0]
                    val timePart = parts[1].substring(0, 5)
                    "$datePart $timePart"
                } else {
                    dateStr
                }
            } catch (e: Exception) {
                dateStr
            }
        }
    }

    class VisitDiffCallback : DiffUtil.ItemCallback<VisitResponse>() {
        override fun areItemsTheSame(oldItem: VisitResponse, newItem: VisitResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VisitResponse, newItem: VisitResponse): Boolean {
            return oldItem == newItem
        }
    }
}
