package com.bulgariaexplorer.app.data.repository

import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.data.remote.dto.UserResponse

class UserRepository {

    suspend fun getCurrentUser(): Result<UserResponse> {
        return try {
            val response = RetrofitClient.userApi.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
