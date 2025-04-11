package com.example.nomsy

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nomsy.ui.screens.LoginScreen
import com.example.nomsy.ui.screens.OnboardingAgeScreen
import com.example.nomsy.ui.screens.OnboardingFitnessGoalScreen
import com.example.nomsy.ui.screens.OnboardingHeightScreen
import com.example.nomsy.ui.screens.OnboardingNameScreen
import com.example.nomsy.ui.screens.OnboardingNutritionScreen
import com.example.nomsy.ui.screens.OnboardingWeightScreen
import com.example.nomsy.ui.screens.OnboardingWelcomeScreen
import com.example.nomsy.ui.screens.RegisterScreen
import com.example.nomsy.ui.screens.RegistrationCompleteScreen
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
         composable("register"){ RegisterScreen(navController) }
        // Onboarding flow
        composable("onboarding_welcome") {
            OnboardingWelcomeScreen(navController)
        }
        composable("onboarding_name") {
            val authViewModel: AuthViewModel = viewModel()
            OnboardingNameScreen(navController, authViewModel)
        }
        composable("onboarding_age") {
            val authViewModel: AuthViewModel = viewModel()
            OnboardingAgeScreen(navController, authViewModel)
        }
        composable("onboarding_height") {
            val authViewModel: AuthViewModel = viewModel()
            OnboardingHeightScreen(navController, authViewModel)
        }
        composable("onboarding_weight") {
            val authViewModel: AuthViewModel = viewModel()
            OnboardingWeightScreen(navController, authViewModel)
        }
        composable("onboarding_fitness_goal") {
            val authViewModel: AuthViewModel = viewModel()
            OnboardingFitnessGoalScreen(navController, authViewModel)
        }
        composable("onboarding_nutrition") {
            val authViewModel: AuthViewModel = viewModel()
            OnboardingNutritionScreen(navController, authViewModel)
        }

        // Registration complete
        composable("registration_complete") {
            val authViewModel: AuthViewModel = viewModel()
            RegistrationCompleteScreen(navController, authViewModel)
        }

//        composable("home") {
//            HomeScreen(navController)
//        }
        // …
    }
}