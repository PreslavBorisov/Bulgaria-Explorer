package com.bulgariaexplorer.app.ui.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.remote.dto.LeaderboardResponse
import com.bulgariaexplorer.app.databinding.ItemLeaderboardBinding

class LeaderboardAdapter : ListAdapter<LeaderboardResponse, LeaderboardAdapter.ViewHolder>(LeaderboardDiffCallback()) {

    var currentUserId: Long? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), getItem(position).userId == currentUserId)
    }

    class ViewHolder(private val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: LeaderboardResponse, isCurrentUser: Boolean) {
            val context = itemView.context
            val card = binding.root

            if (isCurrentUser) {
                card.setCardBackgroundColor(resolveColor(context, com.google.android.material.R.attr.colorPrimaryContainer))
                card.strokeColor = resolveColor(context, androidx.appcompat.R.attr.colorPrimary)
                card.strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            } else {
                card.setCardBackgroundColor(resolveColor(context, com.google.android.material.R.attr.colorSurface))
                card.strokeWidth = 0
            }

            binding.apply {
                tvRank.text = String.format("#${entry.rank ?: (absoluteAdapterPosition + 1)}")
                tvUsername.text = entry.username
                tvLevel.text = context.getString(R.string.leaderboard_level_format, entry.level)
                tvPoints.text = context.getString(R.string.leaderboard_points_format, entry.totalPoints)
            }
        }

        private fun resolveColor(context: android.content.Context, attr: Int): Int {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(attr, typedValue, true)
            return typedValue.data
        }
    }

    class LeaderboardDiffCallback : DiffUtil.ItemCallback<LeaderboardResponse>() {
        override fun areItemsTheSame(oldItem: LeaderboardResponse, newItem: LeaderboardResponse): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: LeaderboardResponse, newItem: LeaderboardResponse): Boolean {
            return oldItem == newItem
        }
    }
}
