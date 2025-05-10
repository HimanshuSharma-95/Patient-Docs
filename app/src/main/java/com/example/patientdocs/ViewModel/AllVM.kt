package com.example.patientdocs.ViewModel

import androidx.paging.cachedIn
import com.example.patientdocs.Model.FormDataReceive
import com.example.patientdocs.Model.FormDataSend
import com.example.patientdocs.Repositories.AllRepository
import com.example.patientdocs.Utils.Resource
import com.example.patientdocs.Utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.patientdocs.Utils.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class AllVM @Inject constructor(
    private val allRepo : AllRepository
): UiEventsVM(){

    private val _registerPatientData = MutableStateFlow<Resource<FormDataReceive>>(Resource.Nothing())
    val registerPatientData = _registerPatientData.asStateFlow()

    private val _getPatientData = MutableStateFlow<Resource<FormDataReceive>>(Resource.Nothing())
    val getPatientData = _getPatientData.asStateFlow()

    val patientsList = allRepo.getAllPatients().cachedIn(
        viewModelScope
    )


    fun registerPatient(data: FormDataSend){

        viewModelScope.launch(Dispatchers.IO){

            emitUiEvent(UiEvent.Loading(Action.Register))

            val response = allRepo.registerPatient(data)
            _registerPatientData.value = response

            when(response){
                is Resource.Error -> {
                    emitUiEvent(UiEvent.Error(Action.Register,response.error ?: "Couldn't Register Patient"))
                }
                is Resource.Success -> {
                    emitUiEvent(UiEvent.Success(Action.Register))
                }
                else -> Unit
            }

        }

    }


    fun getPatient(id:String){

        viewModelScope.launch(Dispatchers.IO){

            emitUiEvent(UiEvent.Loading(Action.GetPatient))

            val response = allRepo.getPatient(id)
            _getPatientData.value = response

            when(response){
                is Resource.Error -> {
                    emitUiEvent(UiEvent.Error(Action.GetPatient,response.error ?: "Couldn't Load Patient"))
                }
                is Resource.Success -> {
                    emitUiEvent(UiEvent.Success(Action.GetPatient))
                }
                else -> Unit
            }

        }
    }

}