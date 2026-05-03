package com.bulgariaexplorer.app.data.remote.dto

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val totalPoints: Int,
    val level: Int,
    val streakDays: Int,
    val role: String
)
