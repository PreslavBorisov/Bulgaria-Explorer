package com.bulgariaexplorer.app.data.remote.dto.admin

data class AdminPoiRequest(
    val title: String,
    val shortDescription: String?,
    val fullDescription: String?,
    val latitude: Double,
    val longitude: Double,
    val categoryId: Long,
    val address: String?,
    val city: String?,
    val region: String?,
    val openingHours: String?,
    val sourceUrl: String?,
    val rewardPoints: Int?,
    val active: Boolean
)
