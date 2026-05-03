package com.bulgariaexplorer.app.data.remote.dto

data class ErrorResponse(
    val message: String?,
    val error: String?,
    val status: Int?
)
