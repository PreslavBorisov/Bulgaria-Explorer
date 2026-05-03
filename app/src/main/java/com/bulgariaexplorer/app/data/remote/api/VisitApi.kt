package com.bulgariaexplorer.app.data.remote.api

import com.bulgariaexplorer.app.data.remote.dto.CreateVisitRequest
import com.bulgariaexplorer.app.data.remote.dto.UploadResponse
import com.bulgariaexplorer.app.data.remote.dto.VisitResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface VisitApi {

    @POST("api/visits")
    suspend fun createVisit(
        @Body request: CreateVisitRequest
    ): Response<VisitResponse>

    @GET("api/visits/me")
    suspend fun getMyVisits(): Response<List<VisitResponse>>

    @Multipart
    @POST("api/uploads/image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>
}
