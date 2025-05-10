package com.example.patientdocs.Model


data class ApiResponse<T>(
    val data: T,
    val message: String,
    val statuscode: Int,
    val success: Boolean
)