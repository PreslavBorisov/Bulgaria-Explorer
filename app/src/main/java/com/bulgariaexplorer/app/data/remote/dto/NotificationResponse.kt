package com.bulgariaexplorer.app.data.remote.dto

data class NotificationResponse(
    val id: Long,
    val title: String,
    val message: String,
    val type: String,
    val read: Boolean,
    val referenceId: Long?,
    val createdAt: String?
)
