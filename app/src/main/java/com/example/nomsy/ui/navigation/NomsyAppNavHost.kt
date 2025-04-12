package com.example.nomsy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nomsy.ui.screens.ProfileScreen
import com.example.nomsy.ui.screens.auth.LoginScreen
import com.example.nomsy.ui.screens.auth.OnboardingAgeScreen
import com.example.nomsy.ui.screens.auth.OnboardingFitnessGoalScreen
import com.example.nomsy.ui.screens.auth.OnboardingHeightScreen
import com.example.nomsy.ui.screens.auth.OnboardingNameScreen
import com.example.nomsy.ui.screens.auth.OnboardingNutritionScreen
import com.example.nomsy.ui.screens.auth.OnboardingWeightScreen
import com.example.nomsy.ui.screens.auth.OnboardingWelcomeScreen
import com.example.nomsy.ui.screens.auth.RegisterScreen
import com.example.nomsy.ui.screens.auth.RegistrationCompleteScreen
import com.example.nomsy.viewModels.AuthViewModel

@Composable
fun NomsyAppNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "login"
    )  {
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("register") {
            RegisterScreen(navController, authViewModel)
        }
        composable("onboarding_welcome") {
            OnboardingWelcomeScreen(navController)
        }
        composable("onboarding_name") {
            OnboardingNameScreen(navController, authViewModel)
        }
        composable("onboarding_age") {
            OnboardingAgeScreen(navController, authViewModel)
        }
        composable("onboarding_height") {
            OnboardingHeightScreen(navController, authViewModel)
        }
        composable("onboarding_weight") {
            OnboardingWeightScreen(navController, authViewModel)
        }
        composable("onboarding_fitness_goal") {
            OnboardingFitnessGoalScreen(navController, authViewModel)
        }
        composable("onboarding_nutrition") {
            OnboardingNutritionScreen(navController, authViewModel)
        }
        composable("registration_complete") {
            RegistrationCompleteScreen(navController, authViewModel)
        }

        // Profile
        composable("profile") {
            ProfileScreen(navController, authViewModel)
        }

//        composable("home") {
//            HomeScreen(navController)
//        }
        // …
    }
}