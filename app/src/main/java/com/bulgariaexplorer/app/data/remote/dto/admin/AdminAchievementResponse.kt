package com.bulgariaexplorer.app.data.remote.dto.admin

data class AdminAchievementResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val code: String,
    val iconName: String?,
    val targetValue: Int?,
    val active: Boolean,
    val createdAt: String?
)
