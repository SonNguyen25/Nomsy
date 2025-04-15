package com.example.nomsy.ui.navigation

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nomsy.data.local.RecipeDatabase
import com.example.nomsy.data.remote.network.RecipeRetrofitInstance
import com.example.nomsy.data.repository.RecipeRepository
import com.example.nomsy.ui.screens.HomeScreen
import com.example.nomsy.ui.screens.StatisticsScreen
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
import com.example.nomsy.ui.screens.profile.ProfileScreen
import com.example.nomsy.ui.screens.recipes.recipesScreen
import com.example.nomsy.viewModels.AuthViewModel
import com.example.nomsy.viewModels.AuthViewModelFactory
import com.example.nomsy.viewModels.FoodViewModel
import com.example.nomsy.viewModels.HomeViewModel
import com.example.nomsy.viewModels.ProfileViewModel
import com.example.nomsy.viewModels.ProfileViewModelFactory
import com.example.nomsy.viewModels.RecipeViewModel
import com.example.nomsy.viewModels.RecipeViewModelFactory

@Composable
fun NomsyAppNavHost(navController: NavHostController) {
//    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as Application)
    )
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context.applicationContext as Application)
    )

    val homeViewModel: HomeViewModel = viewModel()
    val foodViewModel: FoodViewModel = viewModel()

    // observe the current route
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // routes on the bottom bar
    val bottomBarRoutes = listOf(
        BottomNavItem.Statistics.route,
        BottomNavItem.Home.route,
        BottomNavItem.Recipes.route,
        BottomNavItem.Profile.route
    )
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()


    // Only navigate to "login" when we just logged out and we're not already there
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn && navController.currentBackStackEntry?.destination?.route != "login") {
            navController.navigate("login") {
                // Pop everything up to (and including) "login" so we don't stack multiple logins
                popUpTo("login") { inclusive = true }
            }
        }
    }



    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Authentication flow
            composable("login") { LoginScreen(navController, authViewModel) }
            composable("register") { RegisterScreen(navController, authViewModel) }
            composable("onboarding_welcome") { OnboardingWelcomeScreen(navController) }
            composable("onboarding_name") { OnboardingNameScreen(navController, authViewModel) }
            composable("onboarding_age") { OnboardingAgeScreen(navController, authViewModel) }
            composable("onboarding_height") { OnboardingHeightScreen(navController, authViewModel) }
            composable("onboarding_weight") { OnboardingWeightScreen(navController, authViewModel) }
            composable("onboarding_fitness_goal") {
                OnboardingFitnessGoalScreen(
                    navController,
                    authViewModel
                )
            }
            composable("onboarding_nutrition") {
                OnboardingNutritionScreen(
                    navController,
                    authViewModel
                )
            }
            composable("registration_complete") {
                RegistrationCompleteScreen(
                    navController,
                    authViewModel
                )
            }

            // Main app screens with bottom bar
            composable(BottomNavItem.Statistics.route) { StatisticsScreen() }
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    navController = navController,
                    viewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    authViewModel = authViewModel
                )
            }

            composable(BottomNavItem.Recipes.route) {
                val db = RecipeDatabase.getInstance(context)
                val repository = RecipeRepository(RecipeRetrofitInstance.api, db.recipeDAO())
                val factory = RecipeViewModelFactory(repository)
                val recipeViewModel: RecipeViewModel = viewModel(factory = factory)

                recipesScreen(
                    navController = navController,
                    viewModel = recipeViewModel
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel
                )
            }

            // Edit profile screen – no bottom bar
            composable("edit_profile") {
                EditProfileScreen(
                    navController = navController,
                    profileViewModel = profileViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}