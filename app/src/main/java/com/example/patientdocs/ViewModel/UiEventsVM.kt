package com.example.patientdocs.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientdocs.Utils.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


open class UiEventsVM() : ViewModel(){

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvents.asSharedFlow()

    fun emitUiEvent(uiEvent:UiEvent){
        viewModelScope.launch {
            _uiEvents.emit(uiEvent)
        }
    }

}
