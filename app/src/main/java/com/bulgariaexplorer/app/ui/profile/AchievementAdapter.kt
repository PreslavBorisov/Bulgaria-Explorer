package com.bulgariaexplorer.app.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.remote.dto.AchievementResponse
import com.bulgariaexplorer.app.databinding.ItemAchievementBinding

class AchievementAdapter : ListAdapter<AchievementResponse, AchievementAdapter.ViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(achievement: AchievementResponse) {
            binding.apply {
                tvTitle.text = achievement.title
                tvDescription.text = achievement.description
                tvUnlocked.text = if (achievement.unlockedAt != null) {
                    itemView.context.getString(R.string.achievement_unlocked_format, achievement.unlockedAt.substring(0, 10))
                } else {
                    itemView.context.getString(R.string.achievement_locked)
                }
            }
        }
    }

    class AchievementDiffCallback : DiffUtil.ItemCallback<AchievementResponse>() {
        override fun areItemsTheSame(oldItem: AchievementResponse, newItem: AchievementResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AchievementResponse, newItem: AchievementResponse): Boolean {
            return oldItem == newItem
        }
    }
}
