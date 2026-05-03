package com.bulgariaexplorer.app.data.repository

import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.data.remote.dto.CategoryResponse
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.remote.dto.admin.*

class AdminRepository {

    // Dashboard
    suspend fun getDashboardStats(): Result<DashboardStatsResponse> {
        return try {
            val response = RetrofitClient.adminApi.getDashboardStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при зареждане на статистиките"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Users
    suspend fun getAllUsers(): Result<List<AdminUserResponse>> {
        return try {
            val response = RetrofitClient.adminApi.getAllUsers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при зареждане на потребителите"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changeUserRole(userId: Long, role: String): Result<AdminUserResponse> {
        return try {
            val response = RetrofitClient.adminApi.changeUserRole(
                userId, ChangeRoleRequest(role)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при промяна на ролята"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.adminApi.deleteUser(userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Грешка при изтриване на потребител"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // POIs
    suspend fun getAllPois(): Result<List<PoiResponse>> {
        return try {
            val response = RetrofitClient.adminApi.getAllPois()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при зареждане на обектите"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePoi(poiId: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.adminApi.deletePoi(poiId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Грешка при изтриване на обект"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPoi(request: AdminPoiRequest): Result<PoiResponse> {
        return try {
            val response = RetrofitClient.adminApi.createPoi(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при създаване на обект"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePoi(poiId: Long, request: AdminPoiRequest): Result<PoiResponse> {
        return try {
            val response = RetrofitClient.adminApi.updatePoi(poiId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при обновяване на обект"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<CategoryResponse>> {
        return try {
            val response = RetrofitClient.adminApi.getCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при зареждане на категориите"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Missions
    suspend fun getAllMissions(): Result<List<AdminMissionResponse>> {
        return try {
            val response = RetrofitClient.adminApi.getAllMissions()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при зареждане на мисиите"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createMission(request: AdminMissionRequest): Result<AdminMissionResponse> {
        return try {
            val response = RetrofitClient.adminApi.createMission(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при създаване на мисия"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMission(missionId: Long, request: AdminMissionRequest): Result<AdminMissionResponse> {
        return try {
            val response = RetrofitClient.adminApi.updateMission(missionId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при обновяване на мисия"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMission(missionId: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.adminApi.deleteMission(missionId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Грешка при изтриване на мисия"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Achievements
    suspend fun getAllAchievements(): Result<List<AdminAchievementResponse>> {
        return try {
            val response = RetrofitClient.adminApi.getAllAchievements()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при зареждане на постиженията"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAchievement(request: AdminAchievementRequest): Result<AdminAchievementResponse> {
        return try {
            val response = RetrofitClient.adminApi.createAchievement(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при създаване на постижение"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAchievement(achievementId: Long, request: AdminAchievementRequest): Result<AdminAchievementResponse> {
        return try {
            val response = RetrofitClient.adminApi.updateAchievement(achievementId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Грешка при обновяване на постижение"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAchievement(achievementId: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.adminApi.deleteAchievement(achievementId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Грешка при изтриване на постижение"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Notifications
    suspend fun broadcastNotification(request: SendNotificationRequest): Result<Int> {
        return try {
            val response = RetrofitClient.adminApi.broadcastNotification(request)
            if (response.isSuccessful && response.body() != null) {
                val sent = (response.body()!!["sent"] as? Double)?.toInt() ?: 0
                Result.success(sent)
            } else {
                Result.failure(Exception("Грешка при изпращане на известие"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
