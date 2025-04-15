//package com.example.nomsy.ui.navigation
//
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import com.example.nomsy.ui.screens.HomeScreen
//import com.example.nomsy.ui.screens.StatisticsScreen
//import com.example.nomsy.ui.screens.auth.LoginScreen
//import com.example.nomsy.ui.screens.auth.OnboardingAgeScreen
//import com.example.nomsy.ui.screens.auth.OnboardingFitnessGoalScreen
//import com.example.nomsy.ui.screens.auth.OnboardingHeightScreen
//import com.example.nomsy.ui.screens.auth.OnboardingNameScreen
//import com.example.nomsy.ui.screens.auth.OnboardingNutritionScreen
//import com.example.nomsy.ui.screens.auth.OnboardingWeightScreen
//import com.example.nomsy.ui.screens.auth.OnboardingWelcomeScreen
//import com.example.nomsy.ui.screens.auth.RegisterScreen
//import com.example.nomsy.ui.screens.auth.RegistrationCompleteScreen
//import com.example.nomsy.ui.screens.profile.EditProfileScreen
//import com.example.nomsy.ui.screens.profile.ProfileScreen
//import com.example.nomsy.ui.screens.recipes.recipesScreen
//import com.example.nomsy.viewModels.IAuthViewModel
//import com.example.nomsy.viewModels.IHomeViewModel
//import com.example.nomsy.viewModels.IProfileViewModel
//import com.example.nomsy.viewModels.IRecipeViewModel
//
//
//@Composable
//fun TestableNomsyAppNavHost(
//    authViewModel: IAuthViewModel,
//    profileViewModel: IProfileViewModel,
//    homeViewModel: IHomeViewModel,
//    recipeViewModel: IRecipeViewModel
//) {
//    val navController = rememberNavController()
//
//    // observe the current route
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = backStackEntry?.destination?.route
//
//    // routes on the bottom bar
//    val bottomBarRoutes = listOf(
//        BottomNavItem.Statistics.route,
//        BottomNavItem.Home.route,
//        BottomNavItem.Recipes.route,
//        BottomNavItem.Profile.route
//    )
//    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
//
//    // Navigate to login when logged out
//    LaunchedEffect(isLoggedIn) {
//        if (!isLoggedIn && navController.currentBackStackEntry?.destination?.route != "login") {
//            navController.navigate("login") {
//                // Pop everything up to login
//                popUpTo("login") { inclusive = true }
//            }
//        }
//    }
//
//    Scaffold(
//        bottomBar = {
//            if (currentRoute in bottomBarRoutes) {
//                BottomBar(navController)
//            }
//        }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = "login",
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            // Authentication flow
//            composable("login") { LoginScreen(navController, authViewModel) }
//            composable("register") { RegisterScreen(navController, authViewModel) }
//            composable("onboarding_welcome") { OnboardingWelcomeScreen(navController) }
//            composable("onboarding_name") { OnboardingNameScreen(navController, authViewModel) }
//            composable("onboarding_age") { OnboardingAgeScreen(navController, authViewModel) }
//            composable("onboarding_height") { OnboardingHeightScreen(navController, authViewModel) }
//            composable("onboarding_weight") { OnboardingWeightScreen(navController, authViewModel) }
//            composable("onboarding_fitness_goal") {
//                OnboardingFitnessGoalScreen(
//                    navController,
//                    authViewModel
//                )
//            }
//            composable("onboarding_nutrition") {
//                OnboardingNutritionScreen(
//                    navController,
//                    authViewModel
//                )
//            }
//            composable("registration_complete") {
//                RegistrationCompleteScreen(
//                    navController,
//                    authViewModel
//                )
//            }
//
//            // Main app screens with bottom bar
//            composable(BottomNavItem.Statistics.route) { StatisticsScreen() }
//            composable(BottomNavItem.Home.route) {
//                HomeScreen(
//                    navController = navController,
//                    viewModel = homeViewModel,
//                    profileViewModel = profileViewModel,
//                    authViewModel = authViewModel
//                )
//            }
//
//            composable(BottomNavItem.Recipes.route) {
//                recipesScreen(
//                    navController = navController,
//                    viewModel = recipeViewModel
//                )
//            }
//
//            composable(BottomNavItem.Profile.route) {
//                ProfileScreen(
//                    navController = navController,
//                    authViewModel = authViewModel,
//                    profileViewModel = profileViewModel
//                )
//            }
//
//            // Edit profile screen – no bottom bar
//            composable("edit_profile") {
//                EditProfileScreen(
//                    navController = navController,
//                    profileViewModel = profileViewModel,
//                    authViewModel = authViewModel
//                )
//            }
//        }
//    }
//}