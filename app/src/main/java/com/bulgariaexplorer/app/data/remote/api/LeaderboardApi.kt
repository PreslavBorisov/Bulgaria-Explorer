package com.bulgariaexplorer.app.data.remote.api

import com.bulgariaexplorer.app.data.remote.dto.AchievementResponse
import com.bulgariaexplorer.app.data.remote.dto.LeaderboardResponse
import retrofit2.Response
import retrofit2.http.GET

interface LeaderboardApi {

    @GET("api/leaderboard/top10")
    suspend fun getTop10(): Response<List<LeaderboardResponse>>

    @GET("api/leaderboard/me")
    suspend fun getMe(): Response<LeaderboardResponse>

    @GET("api/achievements/me")
    suspend fun getMyAchievements(): Response<List<AchievementResponse>>
}
