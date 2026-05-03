package com.bulgariaexplorer.app.data.remote.dto.admin

data class AdminMissionResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val missionType: String,
    val targetValue: Int,
    val rewardPoints: Int,
    val categoryName: String?,
    val region: String?,
    val validFrom: String?,
    val validTo: String?,
    val active: Boolean,
    val createdAt: String?
)
