package com.example.patientdocs.Utils

import com.example.patientdocs.Model.ErrorResponse
import com.google.gson.Gson
import retrofit2.Response


fun extractError(response : Response<*>):String{

    try {
        val errorBody = response.errorBody()?.string()
        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
        return errorResponse.message

    }catch(e :Exception){
        return "Some Error Occurred"
    }

}