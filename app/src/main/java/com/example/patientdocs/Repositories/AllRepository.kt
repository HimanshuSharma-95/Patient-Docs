package com.example.patientdocs.Repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.patientdocs.Interface.AllInterface
import com.example.patientdocs.Model.FormDataReceive
import com.example.patientdocs.Model.FormDataSend
import com.example.patientdocs.Model.Patient
import com.example.patientdocs.Utils.Resource
import com.example.patientdocs.Utils.extractError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AllRepository @Inject constructor(
    private val allApi : AllInterface
){

    suspend fun registerPatient(data: FormDataSend): Resource<FormDataReceive> {
        try{
            val response = allApi.registerPatient(data)

            return if(response.isSuccessful && response.body() != null){
                Resource.Success(response.body()!!.data)
            }else{
                Resource.Error(extractError(response))
            }

        }catch(e : Exception){
            return Resource.Error("Couldn't Register Patient")
        }

    }


    suspend fun getPatient(id:String):Resource<FormDataReceive>{
        try{
            val response = allApi.getPatient(id)

            return if(response.isSuccessful && response.body() != null){
                Resource.Success(response.body()!!.data)
            }else{
                Resource.Error(extractError(response))
            }

        }catch(e : Exception){
            return Resource.Error("Couldn't Get Patient")
        }
    }


    fun getAllPatients(): Flow<PagingData<Patient>> {
        return Pager(
            config = PagingConfig(pageSize = 5, initialLoadSize = 5, enablePlaceholders = false),
            pagingSourceFactory = {
                PagingSource(allApi)
            }
        ).flow
    }

}


class PagingSource(private val allApi: AllInterface):PagingSource<Int,Patient>(){


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Patient>{

        return try{
            val page = params.key ?: 1
            val response = allApi.getAllPatients(page)
            val body = response.body()

            if (response.isSuccessful && body != null) {

                val data = body.data
                val patients = data.Patients
                val totalPages = data.totalPages
                val currentPage = data.currentPage

                LoadResult.Page(
                    data = patients,
                    prevKey = if (currentPage > 1) currentPage - 1 else null,
                    nextKey = if (currentPage < totalPages) currentPage + 1 else null
                )

            }else{
                LoadResult.Error(Exception("Error Occured"))
            }

        }catch(e:Exception){
            LoadResult.Error(e)
        }

    }


    override fun getRefreshKey(state: PagingState<Int, Patient>): Int? {

        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    }

}