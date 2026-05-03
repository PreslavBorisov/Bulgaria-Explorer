package com.bulgariaexplorer.app.data.repository

import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.data.remote.dto.MissionProgressResponse
import com.bulgariaexplorer.app.data.remote.dto.MissionResponse

class MissionRepository {

    suspend fun getAllMissions(): Result<List<MissionResponse>> {
        return try {
            val response = RetrofitClient.missionApi.getAllMissions()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch missions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyMissions(): Result<List<MissionProgressResponse>> {
        return try {
            val response = RetrofitClient.missionApi.getMyMissions()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch mission progress"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
