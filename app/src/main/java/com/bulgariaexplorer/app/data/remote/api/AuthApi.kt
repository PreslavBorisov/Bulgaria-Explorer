package com.bulgariaexplorer.app.data.remote.api

import com.bulgariaexplorer.app.data.remote.dto.auth.AuthResponse
import com.bulgariaexplorer.app.data.remote.dto.auth.LoginRequest
import com.bulgariaexplorer.app.data.remote.dto.auth.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}
