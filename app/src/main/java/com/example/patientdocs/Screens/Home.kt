package com.example.patientdocs.Screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.patientdocs.Utils.UiEvent
import com.example.patientdocs.ViewModel.AllVM
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.patientdocs.Model.Patient


@Composable
fun HomeScreen(navController: NavHostController){

    val allVM : AllVM = hiltViewModel()

    val lazyPagingItems = allVM.patientsList.collectAsLazyPagingItems()

    var loading by rememberSaveable{
        mutableStateOf(false)
    }

    var error by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(Unit){
        allVM.patientsList
    }



    if(loading || error.isNotEmpty()){

        Column(
            modifier = Modifier.fillMaxSize() ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            when{
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                error.isNotEmpty() ->{
                    Text(error)
                }
            }
        }

    }else {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            PatientListScreen(
                patientList = lazyPagingItems,
                onCardClick = { patientId ->
                    navController.navigate("patientScreen/$patientId")
                }
            )

        }

    }



    LaunchedEffect(Unit){
        allVM.uiEvent.collect{ event ->
            when(event){
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
                is UiEvent.Idle -> Unit
            }
        }
    }

}


@Composable
fun SimpleCard(formData: Patient, onCardClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clickable { onCardClick(formData._id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formData.fullname,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Billing ID: ${"%05d".format(formData.billingId)}",  // shortened for display
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Doctor: ${formData.doctor}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Phone: ${formData.phone_number}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Department: ${formData.department}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}



@Composable
fun PatientListScreen(
    patientList: LazyPagingItems<Patient>,
    onCardClick: (String) -> Unit
) {
    val state = rememberLazyListState()

    // Handle loading and error states
    val isRefreshing = patientList.loadState.refresh is LoadState.Loading
    val isAppending = patientList.loadState.append is LoadState.Loading
    val refreshError = patientList.loadState.refresh as? LoadState.Error
    val appendError = patientList.loadState.append as? LoadState.Error

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    when {
        isRefreshing -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        refreshError != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${refreshError.error.message}")
            }
            return
        }
    }



    SwipeRefresh(
        onRefresh = {
            patientList.refresh()
        },
        state = swipeRefreshState
    ){

        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "All Patients",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            items(patientList.itemCount) { index ->
                val patient = patientList[index]
                patient?.let {
                    SimpleCard(formData = it, onCardClick = onCardClick)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Show loader when appending more data
            if (isAppending) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }

            // Show error when appending fails
            if (appendError != null) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Load More Error: ${appendError.error.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}