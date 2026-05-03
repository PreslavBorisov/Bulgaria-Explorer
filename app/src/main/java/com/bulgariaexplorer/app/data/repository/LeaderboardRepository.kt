package com.bulgariaexplorer.app.data.repository

import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.data.remote.dto.AchievementResponse
import com.bulgariaexplorer.app.data.remote.dto.LeaderboardResponse

class LeaderboardRepository {

    suspend fun getTop10(): Result<List<LeaderboardResponse>> {
        return try {
            val response = RetrofitClient.leaderboardApi.getTop10()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch leaderboard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<LeaderboardResponse> {
        return try {
            val response = RetrofitClient.leaderboardApi.getMe()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch your rank"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyAchievements(): Result<List<AchievementResponse>> {
        return try {
            val response = RetrofitClient.leaderboardApi.getMyAchievements()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch achievements"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
