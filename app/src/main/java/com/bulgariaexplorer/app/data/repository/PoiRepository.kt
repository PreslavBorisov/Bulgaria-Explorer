package com.bulgariaexplorer.app.data.repository

import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse

class PoiRepository {

    suspend fun getAllPois(): Result<List<PoiResponse>> {
        return try {
            val response = RetrofitClient.poiApi.getAllPois()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch POIs: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPoiById(id: Long): Result<PoiResponse> {
        return try {
            val response = RetrofitClient.poiApi.getPoiById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch POI: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchPois(query: String): Result<List<PoiResponse>> {
        return try {
            val response = RetrofitClient.poiApi.searchPois(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to search POIs: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
