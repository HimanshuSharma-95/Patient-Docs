package com.example.patientdocs.Model

data class FormDataSend(
    val DOB: String,
    val address: String,
    val department: String,
    val doctor: String,
    val email: String,
    val fees: Int,
    val fullname: String,
    val gender: String,
    val paymentMethod: String,
    val phone_number: String,
    val cash_in:Int,
    val cash_out:Int
)