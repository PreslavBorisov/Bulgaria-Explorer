package com.bulgariaexplorer.app.data.repository

import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.data.remote.dto.NotificationResponse

class NotificationRepository {

    suspend fun getNotifications(): Result<List<NotificationResponse>> {
        return try {
            val response = RetrofitClient.notificationApi.getNotifications()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при зареждане на известията"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUnreadCount(): Result<Long> {
        return try {
            val response = RetrofitClient.notificationApi.getUnreadCount()
            if (response.isSuccessful && response.body() != null) {
                val count = response.body()!!["count"] ?: 0L
                Result.success(count)
            } else {
                Result.failure(Exception("Грешка"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsRead(notificationId: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.notificationApi.markAsRead(notificationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Грешка"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val response = RetrofitClient.notificationApi.markAllAsRead()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Грешка"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
