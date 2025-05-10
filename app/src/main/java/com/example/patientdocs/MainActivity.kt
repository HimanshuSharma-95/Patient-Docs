package com.example.patientdocs

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.patientdocs.Model.NavRoutes
import com.example.patientdocs.Screens.HomeScreen
import com.example.patientdocs.Screens.PatientScreen
import com.example.patientdocs.Screens.UploadScreen
import com.example.patientdocs.ui.theme.PatientDocsTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        enableEdgeToEdge()

        setContent {


            val navRoutes = arrayListOf(
                NavRoutes(
                    label = "Home",
                    route = "home",
                    icon = Icons.Default.Home
                ),
                NavRoutes(
                    label = "Register",
                    route = "register",
                    icon = Icons.AutoMirrored.Filled.List
                )
            )

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route



            Scaffold(
                bottomBar = {

                    NavigationBar{

                        navRoutes.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigate(item.route)
                                    {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                    }

                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(item.label)
                                }
                            )
                        }

                    }

                }
            ){

                App(modifier = Modifier.padding(it),navController)

            }

        }
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun App(modifier: Modifier,navController : NavHostController){


    Box(
        modifier = modifier
    ){
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {


            composable("home"){
                HomeScreen(navController)
            }

            composable("register"){
                UploadScreen(navController)
            }

            composable("patientScreen/{id}",
                arguments = listOf( navArgument("id") { type = NavType.StringType})
            ){ backStackEntry ->

                val id = backStackEntry.arguments?.getString("id") ?: ""
                PatientScreen(id)

            }

        }

    }



}

