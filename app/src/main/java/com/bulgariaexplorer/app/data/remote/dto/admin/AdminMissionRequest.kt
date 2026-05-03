package com.bulgariaexplorer.app.data.remote.dto.admin

data class AdminMissionRequest(
    val title: String,
    val description: String?,
    val missionType: String,
    val targetValue: Int,
    val rewardPoints: Int,
    val categoryId: Long?,
    val region: String?,
    val validFrom: String?,
    val validTo: String?,
    val active: Boolean
)
