package com.bulgariaexplorer.app.ui.admin.missions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminMissionResponse
import com.bulgariaexplorer.app.databinding.ItemAdminMissionBinding

class AdminMissionAdapter(
    private var missions: List<AdminMissionResponse>,
    private val onMissionClick: (AdminMissionResponse) -> Unit
) : RecyclerView.Adapter<AdminMissionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAdminMissionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mission = missions[position]
        holder.binding.apply {
            tvMissionTitle.text = mission.title
            tvMissionType.text = mission.missionType
            tvMissionReward.text = "${mission.rewardPoints} точки | Цел: ${mission.targetValue}"
            chipActive.text = if (mission.active) "Активна" else "Неактивна"
            chipActive.setChipBackgroundColorResource(
                if (mission.active) android.R.color.holo_green_light
                else android.R.color.holo_red_light
            )
        }
        holder.itemView.setOnClickListener { onMissionClick(mission) }
    }

    override fun getItemCount() = missions.size

    fun updateMissions(newMissions: List<AdminMissionResponse>) {
        missions = newMissions
        notifyDataSetChanged()
    }
}
