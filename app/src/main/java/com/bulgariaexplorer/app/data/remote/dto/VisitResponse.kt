package com.bulgariaexplorer.app.data.remote.dto

import java.math.BigDecimal

data class VisitResponse(
    val id: Long,
    val poiId: Long,
    val poiTitle: String?,
    val poiImageUrl: String?,
    val visitedAt: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val distanceMeters: BigDecimal,
    val photoUrl: String?,
    val basePoints: Int,
    val bonusPoints: Int,
    val totalAwardedPoints: Int,
    val validationStatus: String?
)
