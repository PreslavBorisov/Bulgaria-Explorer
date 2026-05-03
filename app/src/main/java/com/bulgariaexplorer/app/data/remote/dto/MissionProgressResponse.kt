package com.bulgariaexplorer.app.data.remote.dto

data class MissionProgressResponse(
    val missionId: Long,
    val title: String,
    val progress: Int,
    val target: Int,
    val completed: Boolean
)
