package com.bulgariaexplorer.app.data.remote.dto.admin

data class AdminUserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val role: String,
    val totalPoints: Int,
    val level: Int,
    val streakDays: Int,
    val createdAt: String?
)
