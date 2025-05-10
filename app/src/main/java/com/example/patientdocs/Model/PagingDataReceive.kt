package com.example.patientdocs.Model

data class PagingDataReceive(
    val Patients: List<Patient>,
    val currentPage: Int,
    val totalPages: Int,
    val totalPatients: Int
)