package com.bulgariaexplorer.app.data.remote.dto

data class AchievementResponse(
    val id: Long,
    val code: String,
    val title: String,
    val description: String,
    val iconName: String?,
    val unlockedAt: String?
)
