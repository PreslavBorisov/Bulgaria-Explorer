package com.bulgariaexplorer.app.data.remote.dto.auth

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val points: Int,
    val level: Int,
    val currentStreak: Int,
    val role: String
)
