package com.bulgariaexplorer.app.data.remote.dto

data class PoiResponse(
    val id: Long,
    val title: String?,
    val shortDescription: String?,
    val fullDescription: String?,
    val latitude: Double,
    val longitude: Double,
    val rewardPoints: Int,
    val imageUrl: String?,
    val imageUrls: List<String>?,
    val visitPhotoUrls: List<String>?,
    val categoryName: String?,
    val categoryCode: String?,
    val address: String?,
    val city: String?,
    val region: String?,
    val openingHours: String? = null,
    val sourceUrl: String? = null,
    val active: Boolean,
    val visitCount: Int? = null,
    val difficulty: String? = null
)

