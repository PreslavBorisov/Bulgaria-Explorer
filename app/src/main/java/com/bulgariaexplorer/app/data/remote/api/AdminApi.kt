package com.bulgariaexplorer.app.data.remote.api

import com.bulgariaexplorer.app.data.remote.dto.CategoryResponse
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.remote.dto.admin.*
import retrofit2.Response
import retrofit2.http.*

interface AdminApi {

    // Dashboard
    @GET("api/admin/dashboard/stats")
    suspend fun getDashboardStats(): Response<DashboardStatsResponse>

    // Users
    @GET("api/admin/users")
    suspend fun getAllUsers(): Response<List<AdminUserResponse>>

    @PUT("api/admin/users/{id}/role")
    suspend fun changeUserRole(
        @Path("id") userId: Long,
        @Body request: ChangeRoleRequest
    ): Response<AdminUserResponse>

    @DELETE("api/admin/users/{id}")
    suspend fun deleteUser(
        @Path("id") userId: Long
    ): Response<Unit>

    // POIs
    @GET("api/admin/pois")
    suspend fun getAllPois(): Response<List<PoiResponse>>

    @POST("api/admin/pois")
    suspend fun createPoi(
        @Body request: AdminPoiRequest
    ): Response<PoiResponse>

    @PUT("api/admin/pois/{id}")
    suspend fun updatePoi(
        @Path("id") poiId: Long,
        @Body request: AdminPoiRequest
    ): Response<PoiResponse>

    @DELETE("api/admin/pois/{id}")
    suspend fun deletePoi(
        @Path("id") poiId: Long
    ): Response<Unit>

    // Categories
    @GET("api/pois/categories")
    suspend fun getCategories(): Response<List<CategoryResponse>>

    // Missions
    @GET("api/admin/missions")
    suspend fun getAllMissions(): Response<List<AdminMissionResponse>>

    @POST("api/admin/missions")
    suspend fun createMission(
        @Body request: AdminMissionRequest
    ): Response<AdminMissionResponse>

    @PUT("api/admin/missions/{id}")
    suspend fun updateMission(
        @Path("id") missionId: Long,
        @Body request: AdminMissionRequest
    ): Response<AdminMissionResponse>

    @DELETE("api/admin/missions/{id}")
    suspend fun deleteMission(
        @Path("id") missionId: Long
    ): Response<Unit>

    // Achievements
    @GET("api/admin/achievements")
    suspend fun getAllAchievements(): Response<List<AdminAchievementResponse>>

    @POST("api/admin/achievements")
    suspend fun createAchievement(
        @Body request: AdminAchievementRequest
    ): Response<AdminAchievementResponse>

    @PUT("api/admin/achievements/{id}")
    suspend fun updateAchievement(
        @Path("id") achievementId: Long,
        @Body request: AdminAchievementRequest
    ): Response<AdminAchievementResponse>

    @DELETE("api/admin/achievements/{id}")
    suspend fun deleteAchievement(
        @Path("id") achievementId: Long
    ): Response<Unit>

    // Notifications
    @POST("api/admin/notifications/broadcast")
    suspend fun broadcastNotification(
        @Body request: SendNotificationRequest
    ): Response<Map<String, Any>>
}
