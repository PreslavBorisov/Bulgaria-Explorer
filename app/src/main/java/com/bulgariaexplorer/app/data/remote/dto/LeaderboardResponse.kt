package com.bulgariaexplorer.app.data.remote.dto

data class LeaderboardResponse(
    val rank: Int?,
    val userId: Long,
    val username: String,
    val totalPoints: Int,
    val level: Int,
    val streakDays: Int?
)
