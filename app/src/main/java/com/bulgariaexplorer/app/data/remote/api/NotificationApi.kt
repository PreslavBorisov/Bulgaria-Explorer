package com.bulgariaexplorer.app.data.remote.api

import com.bulgariaexplorer.app.data.remote.dto.NotificationResponse
import retrofit2.Response
import retrofit2.http.*

interface NotificationApi {

    @GET("api/user/notifications")
    suspend fun getNotifications(): Response<List<NotificationResponse>>

    @GET("api/user/notifications/unread")
    suspend fun getUnreadNotifications(): Response<List<NotificationResponse>>

    @GET("api/user/notifications/unread-count")
    suspend fun getUnreadCount(): Response<Map<String, Long>>

    @PUT("api/user/notifications/{id}/read")
    suspend fun markAsRead(
        @Path("id") notificationId: Long
    ): Response<Unit>

    @PUT("api/user/notifications/read-all")
    suspend fun markAllAsRead(): Response<Unit>
}
