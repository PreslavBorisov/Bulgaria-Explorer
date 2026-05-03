package com.bulgariaexplorer.app.data.remote.api

import com.bulgariaexplorer.app.data.remote.dto.UserResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {

    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<UserResponse>
}
