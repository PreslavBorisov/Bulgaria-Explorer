package com.bulgariaexplorer.app.ui.missions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bulgariaexplorer.app.data.remote.dto.MissionProgressResponse
import com.bulgariaexplorer.app.databinding.ItemMissionBinding

class MissionAdapter : ListAdapter<MissionProgressResponse, MissionAdapter.ViewHolder>(MissionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMissionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemMissionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mission: MissionProgressResponse) {
            binding.apply {
                tvTitle.text = mission.title
                tvProgress.text = "${mission.progress} / ${mission.target}"

                val progressPercent = if (mission.target > 0) {
                    (mission.progress.toFloat() / mission.target.toFloat() * 100).toInt()
                } else {
                    0
                }
                progressBarMission.progress = progressPercent
                tvPercentage.text = "$progressPercent%"

                // Set emoji based on mission type
                tvMissionIcon.text = when {
                    mission.title.contains("visit", ignoreCase = true) -> "📍"
                    mission.title.contains("photo", ignoreCase = true) -> "📸"
                    mission.title.contains("explore", ignoreCase = true) -> "🗺️"
                    mission.title.contains("streak", ignoreCase = true) -> "🔥"
                    mission.title.contains("level", ignoreCase = true) -> "⭐"
                    mission.title.contains("point", ignoreCase = true) -> "💎"
                    else -> "🎯"
                }

                if (mission.completed) {
                    tvCompleted.visibility = View.VISIBLE
                } else {
                    tvCompleted.visibility = View.GONE
                }
            }
        }
    }

    class MissionDiffCallback : DiffUtil.ItemCallback<MissionProgressResponse>() {
        override fun areItemsTheSame(oldItem: MissionProgressResponse, newItem: MissionProgressResponse): Boolean {
            return oldItem.missionId == newItem.missionId
        }

        override fun areContentsTheSame(oldItem: MissionProgressResponse, newItem: MissionProgressResponse): Boolean {
            return oldItem == newItem
        }
    }
}
