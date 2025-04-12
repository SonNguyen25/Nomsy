package com.example.nomsy.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nomsy.ui.screens.HomeScreen
import com.example.nomsy.ui.screens.StatisticsScreen
import com.example.nomsy.ui.screens.profile.ProfileScreen
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
import com.example.nomsy.ui.screens.profile.EditProfileScreen
import com.example.nomsy.viewModels.AuthViewModel
import com.example.nomsy.viewModels.HomeViewModel
import com.example.nomsy.viewModels.ProfileViewModel

@Composable
fun NomsyAppNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    // Observe the current route
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Define which routes should show the bottom bar
    val bottomBarRoutes = listOf(
        BottomNavItem.Statistics.route,
        BottomNavItem.Home.route,
        BottomNavItem.Recipes.route,
        BottomNavItem.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = "login",
            modifier         = Modifier.padding(innerPadding)
        ) {
            // Authentication flow
            composable("login")               { LoginScreen(navController, authViewModel) }
            composable("register")            { RegisterScreen(navController, authViewModel) }
            composable("onboarding_welcome")  { OnboardingWelcomeScreen(navController) }
            composable("onboarding_name")     { OnboardingNameScreen(navController, authViewModel) }
            composable("onboarding_age")      { OnboardingAgeScreen(navController, authViewModel) }
            composable("onboarding_height")   { OnboardingHeightScreen(navController, authViewModel) }
            composable("onboarding_weight")   { OnboardingWeightScreen(navController, authViewModel) }
            composable("onboarding_fitness_goal") { OnboardingFitnessGoalScreen(navController, authViewModel) }
            composable("onboarding_nutrition"){ OnboardingNutritionScreen(navController, authViewModel) }
            composable("registration_complete") { RegistrationCompleteScreen(navController, authViewModel) }

            // Main app screens with bottom bar
            composable(BottomNavItem.Statistics.route) { StatisticsScreen() }
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    navController = navController,
                    viewModel = homeViewModel,
                    onAddFoodClick = { navController.navigate("add_food") },
                    authViewModel = authViewModel,
                )
            }
//            composable(BottomNavItem.Recipes.route)    { RecipesScreen(navController) }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    navController       = navController,
                    authViewModel       = authViewModel,
                    profileViewModel    = profileViewModel
                )
            }




            // Edit profile screen – no bottom bar
            composable("edit_profile") {
                EditProfileScreen(
                    navController       = navController,
                    profileViewModel    = profileViewModel,
                    authViewModel       = authViewModel
                )
            }
        }
    }
}