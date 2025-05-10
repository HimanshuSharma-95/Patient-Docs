package com.example.patientdocs.Screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.patientdocs.Model.FormDataSend
import com.example.patientdocs.Utils.UiEvent
import com.example.patientdocs.ViewModel.AllVM


@Composable
fun UploadScreen(navController: NavHostController) {

    val allVM: AllVM = hiltViewModel()
    val registerPatientData = allVM.registerPatientData.collectAsState()

    var fullName by rememberSaveable { mutableStateOf("") }
    var dob by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var department by rememberSaveable { mutableStateOf("") }
    var doctor by rememberSaveable { mutableStateOf("") }
    var fees by rememberSaveable { mutableStateOf("") }
    var paymentMethod by rememberSaveable { mutableStateOf("") }

    var cashIn by rememberSaveable { mutableStateOf("") }
    var cashOut by rememberSaveable { mutableStateOf("") }

    var loading by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }
    var attemptedSubmit by rememberSaveable { mutableStateOf(false) }

    val isDobValid = dob.matches(Regex("^\\d{4}-\\d{2}-\\d{2}\$"))
    val isPhoneValid = phoneNumber.matches(Regex("^\\d{10}$"))
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFeesValid = fees.toIntOrNull() != null
    val isCashIn_Valid = cashIn.toIntOrNull() != null

    var overAllFieldsFilled by rememberSaveable{
        mutableStateOf(false)
    }

    var overAllFormValid by rememberSaveable{
        mutableStateOf(false)
    }

    val allPrimaryFieldsFilled = fullName.isNotBlank() &&
            dob.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            gender.isNotBlank() &&
            address.isNotBlank() &&
            email.isNotBlank() &&
            department.isNotBlank() &&
            doctor.isNotBlank() &&
            fees.isNotBlank() &&
            paymentMethod.isNotBlank()

    val allCashFieldsFilled = cashIn.isNotEmpty()

    overAllFieldsFilled = if(paymentMethod == "cash"){
        allPrimaryFieldsFilled && allCashFieldsFilled
    }else{
        allPrimaryFieldsFilled
    }

    val isPrimaryFormValid = overAllFieldsFilled && isDobValid && isEmailValid && isFeesValid && isPhoneValid

    overAllFormValid = if(paymentMethod == "cash"){
        isPrimaryFormValid && isCashIn_Valid
    }else{
        isPrimaryFormValid
    }


    LaunchedEffect(Unit) {
        allVM.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Loading -> {
                    loading = true
                    error = ""
                }
                is UiEvent.Error -> {
                    loading = false
                    error = event.error
                }
                is UiEvent.Success -> {
                    loading = false
                    error = ""
                    val id = registerPatientData.value.data?._id
                    if (!id.isNullOrEmpty()) {
                        navController.navigate("patientScreen/$id")
                    }
                }
                else -> Unit
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 0.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register Patient",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 20.dp),
            fontWeight =  FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField("Full Name", fullName) { fullName = it }
        InputField(
            "DOB (yyyy-mm-dd)",
            dob,
            attemptedSubmit && !isDobValid,
            "Date must be yyyy-mm-dd"
        ) { dob = it }


        InputField("Phone Number", phoneNumber, attemptedSubmit && !isPhoneValid, "Enter a valid 10-digit number") { phoneNumber = it }

        DropDownField(
            label = "Gender",
            options = listOf("male", "female", "other"),
            selectedOption = gender,
            onOptionSelected = { gender = it }
        )

        InputField("Address", address) { address = it }
        InputField("Email", email, attemptedSubmit && !isEmailValid, "Invalid email") { email = it }

        DropDownField(
            label = "Department",
            options = listOf("Cardiology", "Neurology", "Orthopedics", "General"),
            selectedOption = department,
            onOptionSelected = { department = it }
        )

        InputField("Doctor", doctor) { doctor = it }

        InputField("Fees", fees, attemptedSubmit && !isFeesValid, "Enter a valid number") {
            fees = it
        }

        DropDownField(
            label = "Payment Method",
            options = listOf("cash", "card", "upi"),
            selectedOption = paymentMethod,
            onOptionSelected = { paymentMethod = it }
        )

        if(paymentMethod == "cash"){
            InputField("Cash In", cashIn,attemptedSubmit && !isCashIn_Valid,"Enter a valid number") { cashIn = it }
        }


        if (loading) CircularProgressIndicator(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 15.dp)
                .size(50.dp),
            color = MaterialTheme.colorScheme.primary
        )

        if (error.isNotEmpty()) {
            Text(error, modifier = Modifier.padding(top = 5.dp, bottom = 15.dp) ,color = MaterialTheme.colorScheme.error)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            Button(
                onClick = {
                    fullName = ""
                    dob = ""
                    phoneNumber = ""
                    gender = ""
                    address = ""
                    email = ""
                    department = ""
                    doctor = ""
                    fees = ""
                    paymentMethod = ""
                    attemptedSubmit = false
                    error = ""
                    cashIn = ""
                    cashOut = ""
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Clear", style = MaterialTheme.typography.labelLarge)
            }

            Button(
                onClick = {
                    attemptedSubmit = true
                    if (overAllFormValid) {
                        val patient = FormDataSend(
                            fullname = fullName,
                            DOB = dob,
                            phone_number = phoneNumber,
                            gender = gender,
                            address = address,
                            email = email,
                            department = department,
                            doctor = doctor,
                            fees = fees.toIntOrNull() ?: 0,
                            paymentMethod = paymentMethod,
                            cash_in = if(paymentMethod == "cash"){
                                cashIn.toIntOrNull() ?: 0
                            }else{
                                fees.toIntOrNull() ?: 0
                            },
                            cash_out = if(paymentMethod == "cash"){
                                ((cashIn.toIntOrNull() ?:0 )-(fees.toIntOrNull() ?: 0))
                            }else{
                                0
                            }
                        )
                        allVM.registerPatient(patient)
                    }
                },
                enabled = overAllFieldsFilled && !loading,
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Submit", style = MaterialTheme.typography.labelLarge)
            }


        }
    }
}



@Composable
fun InputField(
    label: String,
    value: String,
    isError: Boolean = false,
    errorText: String = "",
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
        ,
        shape = RoundedCornerShape(12.dp),
        supportingText = {
            if (isError) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(bottom = 15.dp)
            ,
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selection ->
                DropdownMenuItem(
                    text = { Text(selection) },
                    onClick = {
                        onOptionSelected(selection)
                        expanded = false
                    }
                )
            }
        }
    }
}