package com.bulgariaexplorer.app.data.remote.dto.admin

data class DashboardStatsResponse(
    val totalUsers: Long,
    val totalPois: Long,
    val totalVisits: Long,
    val totalMissions: Long,
    val totalAchievements: Long,
    val activePois: Long,
    val activeUsers: Long
)
