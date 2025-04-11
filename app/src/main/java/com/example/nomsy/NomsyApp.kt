package com.example.nomsy

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nomsy.ui.screens.LoginScreen
import com.example.nomsy.viewModels.AuthViewModel

@Composable
fun NomsyApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            val authViewModel: AuthViewModel = viewModel()
            LoginScreen(
                navController   = navController,
                authViewModel   = authViewModel
            )
        }
        // add more routes here:
        // composable("home")    { HomeScreen() }
        // composable("register"){ RegisterScreen(navController) }
        // …
    }
}