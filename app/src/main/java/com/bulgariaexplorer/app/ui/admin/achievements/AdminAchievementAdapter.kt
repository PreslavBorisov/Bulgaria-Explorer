package com.bulgariaexplorer.app.ui.admin.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminAchievementResponse
import com.bulgariaexplorer.app.databinding.ItemAdminAchievementBinding

class AdminAchievementAdapter(
    private var achievements: List<AdminAchievementResponse>,
    private val onAchievementClick: (AdminAchievementResponse) -> Unit
) : RecyclerView.Adapter<AdminAchievementAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAdminAchievementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.binding.apply {
            tvAchievementTitle.text = achievement.title
            tvAchievementCode.text = achievement.code
            tvAchievementTarget.text = "Цел: ${achievement.targetValue ?: "-"}"
            chipActive.text = if (achievement.active) "Активно" else "Неактивно"
            chipActive.setChipBackgroundColorResource(
                if (achievement.active) android.R.color.holo_green_light
                else android.R.color.holo_red_light
            )
        }
        holder.itemView.setOnClickListener { onAchievementClick(achievement) }
    }

    override fun getItemCount() = achievements.size

    fun updateAchievements(newAchievements: List<AdminAchievementResponse>) {
        achievements = newAchievements
        notifyDataSetChanged()
    }
}
