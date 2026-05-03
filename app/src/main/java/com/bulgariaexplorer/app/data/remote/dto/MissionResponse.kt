package com.bulgariaexplorer.app.data.remote.dto

data class MissionResponse(
    val id: Long,
    val title: String,
    val description: String,
    val missionType: String,
    val targetValue: Int,
    val rewardPoints: Int,
    val active: Boolean
)
