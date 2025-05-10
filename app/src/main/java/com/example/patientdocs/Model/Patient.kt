package com.example.patientdocs.Model

data class Patient(
    val DOB: String,
    val __v: Int,
    val _id: String,
    val address: String,
    val billingId: Int,
    val cash_in: Int,
    val cash_out: Int,
    val createdAt: String,
    val department: String,
    val doctor: String,
    val email: String,
    val fees: Int,
    val fullname: String,
    val gender: String,
    val patient_id: Int,
    val paymentMethod: String,
    val phone_number: String,
    val updatedAt: String
)