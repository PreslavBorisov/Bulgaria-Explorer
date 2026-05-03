package com.bulgariaexplorer.app.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.NotificationResponse
import com.bulgariaexplorer.app.data.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationsViewModel(private val notificationRepository: NotificationRepository) : ViewModel() {

    sealed class NotificationsState {
        data object Loading : NotificationsState()
        data class Success(val notifications: List<NotificationResponse>) : NotificationsState()
        data class Error(val message: String) : NotificationsState()
    }

    private val _notificationsState = MutableLiveData<NotificationsState>()
    val notificationsState: LiveData<NotificationsState> = _notificationsState

    private val _unreadCount = MutableLiveData<Long>(0)
    val unreadCount: LiveData<Long> = _unreadCount

    fun loadNotifications() {
        _notificationsState.value = NotificationsState.Loading
        viewModelScope.launch {
            notificationRepository.getNotifications()
                .onSuccess { _notificationsState.value = NotificationsState.Success(it) }
                .onFailure { _notificationsState.value = NotificationsState.Error(it.message ?: "Грешка") }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            notificationRepository.getUnreadCount()
                .onSuccess { _unreadCount.value = it }
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
                .onSuccess { loadNotifications() }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
                .onSuccess { loadNotifications() }
        }
    }
}
