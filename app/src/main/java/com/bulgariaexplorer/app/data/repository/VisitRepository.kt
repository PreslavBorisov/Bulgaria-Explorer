package com.bulgariaexplorer.app.data.repository

import android.content.Context
import android.net.Uri
import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.data.remote.dto.CreateVisitRequest
import com.bulgariaexplorer.app.data.remote.dto.ErrorResponse
import com.bulgariaexplorer.app.data.remote.dto.UploadResponse
import com.bulgariaexplorer.app.data.remote.dto.VisitResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class VisitRepository(private val context: Context) {

    private val gson = Gson()

    suspend fun uploadImage(imageUri: Uri): Result<UploadResponse> {
        return try {
            // Create temp file from URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { output ->
                inputStream?.copyTo(output)
            }
            inputStream?.close()

            val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

            val response = RetrofitClient.visitApi.uploadImage(multipartBody)

            tempFile.delete()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createVisit(request: CreateVisitRequest): Result<VisitResponse> {
        return try {
            val response = RetrofitClient.visitApi.createVisit(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyVisits(): Result<List<VisitResponse>> {
        return try {
            val response = RetrofitClient.visitApi.getMyVisits()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) {
            return "An error occurred"
        }

        return try {
            val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
            errorResponse.message ?: errorResponse.error ?: "An error occurred"
        } catch (e: Exception) {
            // If JSON parsing fails, return the raw error body
            errorBody
        }
    }
}
