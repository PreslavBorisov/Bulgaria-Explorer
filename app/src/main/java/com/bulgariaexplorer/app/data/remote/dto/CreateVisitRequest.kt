package com.bulgariaexplorer.app.data.remote.dto

import java.math.BigDecimal

data class CreateVisitRequest(
    val poiId: Long,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val distanceMeters: BigDecimal,
    val photoUrl: String
)
