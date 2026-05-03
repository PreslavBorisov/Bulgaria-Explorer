package com.bulgariaexplorer.app.ui.admin.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminUserResponse
import com.bulgariaexplorer.app.databinding.ItemAdminUserBinding

class AdminUserAdapter(
    private var users: List<AdminUserResponse>,
    private val onUserClick: (AdminUserResponse) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAdminUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.binding.apply {
            tvUsername.text = user.username
            tvEmail.text = user.email
            tvPoints.text = "${user.totalPoints} т. | Ниво ${user.level}"
            chipRole.text = user.role
            chipRole.setChipBackgroundColorResource(
                if (user.role == "ADMIN") android.R.color.holo_red_light
                else android.R.color.holo_blue_light
            )
        }
        holder.itemView.setOnClickListener { onUserClick(user) }
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<AdminUserResponse>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
