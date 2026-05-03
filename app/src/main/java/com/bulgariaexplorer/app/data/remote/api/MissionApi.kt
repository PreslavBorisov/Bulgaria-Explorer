package com.bulgariaexplorer.app.data.remote.api

import com.bulgariaexplorer.app.data.remote.dto.MissionProgressResponse
import com.bulgariaexplorer.app.data.remote.dto.MissionResponse
import retrofit2.Response
import retrofit2.http.GET

interface MissionApi {

    @GET("api/missions")
    suspend fun getAllMissions(): Response<List<MissionResponse>>

    @GET("api/missions/me")
    suspend fun getMyMissions(): Response<List<MissionProgressResponse>>
}
