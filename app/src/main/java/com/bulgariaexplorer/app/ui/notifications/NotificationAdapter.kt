package com.bulgariaexplorer.app.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bulgariaexplorer.app.data.remote.dto.NotificationResponse
import com.bulgariaexplorer.app.databinding.ItemNotificationBinding

class NotificationAdapter(
    private var notifications: List<NotificationResponse>,
    private val onNotificationClick: (NotificationResponse) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.binding.apply {
            tvTitle.text = notification.title
            tvMessage.text = notification.message
            tvTime.text = formatTime(notification.createdAt)

            val alpha = if (notification.read) 0.6f else 1.0f
            root.alpha = alpha

            ivUnread.visibility = if (notification.read) android.view.View.GONE else android.view.View.VISIBLE
        }
        holder.itemView.setOnClickListener { onNotificationClick(notification) }
    }

    override fun getItemCount() = notifications.size

    fun updateNotifications(newNotifications: List<NotificationResponse>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    private fun formatTime(createdAt: String?): String {
        if (createdAt == null) return ""
        return try {
            // Format: "2026-04-16T10:30:00"
            val parts = createdAt.split("T")
            if (parts.size == 2) {
                val date = parts[0]  // 2026-04-16
                val time = parts[1].substring(0, 5)  // 10:30
                "$date $time"
            } else {
                createdAt
            }
        } catch (e: Exception) {
            createdAt
        }
    }
}
