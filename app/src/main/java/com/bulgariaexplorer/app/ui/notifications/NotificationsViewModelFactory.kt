package com.bulgariaexplorer.app.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.NotificationRepository

class NotificationsViewModelFactory(
    private val notificationRepository: NotificationRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationsViewModel(notificationRepository) as T
    }
}
