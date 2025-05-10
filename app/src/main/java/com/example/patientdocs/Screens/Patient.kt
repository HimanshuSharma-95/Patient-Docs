package com.example.patientdocs.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.patientdocs.Model.FormDataReceive
import com.example.patientdocs.Utils.UiEvent
import com.example.patientdocs.Utils.createPatientPdf
import com.example.patientdocs.ViewModel.AllVM


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PatientScreen(id: String) {

    val allVM: AllVM = hiltViewModel()
    val patientState by allVM.getPatientData.collectAsState()

    var loading by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }


    LaunchedEffect(id) {
        allVM.getPatient(id)
    }

    LaunchedEffect(Unit) {
        allVM.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Error -> {
                    loading = false
                    error = event.error
                }
                is UiEvent.Loading -> {
                    loading = true
                    error = ""
                }
                is UiEvent.Success -> {
                    loading = false
                    error = ""
                }
                else -> Unit
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp), // Set padding for full-screen effect
        contentAlignment = Alignment.Center
    ) {
        when {
            loading -> CircularProgressIndicator(modifier = Modifier.size(50.dp), color = MaterialTheme.colorScheme.primary)
            error.isNotEmpty() -> Text(error, color = MaterialTheme.colorScheme.error)
            else -> patientState.data?.let { PatientDetailsPDFStyle(it) }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PatientDetailsPDFStyle(patient: FormDataReceive) {

    val context = LocalContext.current


    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(10.dp)
        .verticalScroll(rememberScrollState())
//            .padding(10.dp)
    ) {

        Text(
            text = "OPD Receipt",
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
            ,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp

        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 2.dp, color = androidx.compose.ui.graphics.Color.LightGray)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
//                .background(Color.White)
//                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = "Date : ${patient.createdAt.take(10)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 5.dp)
            )


            Text(
//                    text = "Billing_id : ${patient.billingId}",
                text =  "Billing ID: ${"%05d".format(patient.billingId)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(15.dp)
        ){

            Text(
                text = "Patient Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            PatientField(label = "Patient ID", value = patient.patient_id.toString())
            PatientField(label = "Name", value = patient.fullname)
            PatientField(label = "DOB", value = patient.DOB.take(10))
            PatientField(label = "Gender", value = patient.gender)
            PatientField(label = "Email", value = patient.email)
            PatientField(label = "Phone", value = patient.phone_number)
            PatientField(label = "Address", value = patient.address)
        }

        Spacer(modifier = Modifier.height(10.dp))



        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(15.dp)
        ) {

            Text(
                text = "Doctor's Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            PatientField(label = "Doctor", value = patient.doctor)
            PatientField(label = "Department", value = patient.department)

        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(15.dp)
        ) {


            Text(
                text = "Payment Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            PatientField(label = "Paid via", value = patient.paymentMethod)
            PatientField(label = "Fees", value = patient.fees.toString())
            PatientField(label = "Cash In", value = patient.cash_in.toString())

            if(patient.paymentMethod == "cash"){
                PatientField(label = "Cash Out", value = patient.cash_out.toString())
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { createPatientPdf(context, patient) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Download PDF")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun PatientField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
