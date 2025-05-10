package com.example.patientdocs.Model

data class ErrorResponse(
    val statusCode: Int,
    val data: Any?,
    val success: Boolean,
    val errors: List<Any>,
    val message: String
)
