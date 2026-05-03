package com.bulgariaexplorer.app.data.repository

import com.bulgariaexplorer.app.data.local.TokenManager
import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.data.remote.dto.auth.AuthResponse
import com.bulgariaexplorer.app.data.remote.dto.auth.LoginRequest
import com.bulgariaexplorer.app.data.remote.dto.auth.RegisterRequest

class AuthRepository(private val tokenManager: TokenManager) {

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = RetrofitClient.authApi.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveToken(authResponse.token)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = RetrofitClient.authApi.register(
                RegisterRequest(username, email, password)
            )
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveToken(authResponse.token)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }
}
