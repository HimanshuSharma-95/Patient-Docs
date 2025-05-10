package com.example.patientdocs.Interface

import com.example.patientdocs.Model.ApiResponse
import com.example.patientdocs.Model.FormDataReceive
import com.example.patientdocs.Model.FormDataSend
import com.example.patientdocs.Model.PagingDataReceive
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AllInterface {

    @POST("patient/register")
    suspend fun registerPatient(@Body formData: FormDataSend) : retrofit2.Response<ApiResponse<FormDataReceive>>

    @GET("patient/getpatient/{id}")
    suspend fun getPatient(@Path("id") id :String ) : retrofit2.Response<ApiResponse<FormDataReceive>>

    @GET("patient/allpatients")
    suspend fun getAllPatients(@Query("page") page : Int) : retrofit2.Response<ApiResponse<PagingDataReceive>>


}