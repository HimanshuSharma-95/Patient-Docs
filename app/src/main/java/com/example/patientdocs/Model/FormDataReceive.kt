package com.example.patientdocs.Model

data class FormDataReceive(

    val DOB: String,
    val patient_id:Int,
    val billingId:Int,
    val __v: Int,
    val _id: String,
    val address: String,
    val createdAt: String,
    val department: String,
    val doctor: String,
    val email: String,
    val fees: Int,
    val fullname: String,
    val gender: String,
    val paymentMethod: String,
    val phone_number: String,
    val updatedAt: String,
    val cash_in:Int,
    val cash_out:Int


)