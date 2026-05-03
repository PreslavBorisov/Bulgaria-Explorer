package com.bulgariaexplorer.app.data.remote.dto

data class FavoriteResponse(
    val id: Long,
    val poiId: Long,
    val poiTitle: String?,
    val categoryName: String?,
    val city: String?,
    val region: String?,
    val imageUrl: String?,
    val rewardPoints: Int? = null
)
