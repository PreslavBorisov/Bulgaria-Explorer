package com.bulgariaexplorer.app.data.repository

import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.data.remote.dto.FavoriteResponse

class FavoriteRepository {

    suspend fun getFavorites(): Result<List<FavoriteResponse>> {
        return try {
            val response = RetrofitClient.favoriteApi.getFavorites()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch favorites: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorite(poiId: Long): Result<FavoriteResponse> {
        return try {
            val response = RetrofitClient.favoriteApi.addFavorite(poiId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to add favorite: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(poiId: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.favoriteApi.removeFavorite(poiId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove favorite: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
