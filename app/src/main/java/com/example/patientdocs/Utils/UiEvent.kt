package com.example.patientdocs.Utils


sealed class UiEvent{
    class Success(val action: Action) : UiEvent()
    class Error(val action: Action, val error : String) : UiEvent()
    class Loading(val action: Action) : UiEvent()
    class Idle(val action: Action) : UiEvent()
}


enum class Action(){
    AllPatients,Register,GetPatient
}