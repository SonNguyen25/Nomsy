package nom.nom.nomsy.ui.navigation

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
import nom.nom.nomsy.data.local.RecipeDatabase
import nom.nom.nomsy.data.remote.network.RecipeRetrofitInstance
import nom.nom.nomsy.data.repository.RecipeRepository
import nom.nom.nomsy.ui.screens.HomeScreen
import nom.nom.nomsy.ui.screens.StatisticsScreen
import nom.nom.nomsy.ui.screens.auth.LoginScreen
import nom.nom.nomsy.ui.screens.auth.OnboardingAgeScreen
import nom.nom.nomsy.ui.screens.auth.OnboardingFitnessGoalScreen
import nom.nom.nomsy.ui.screens.auth.OnboardingHeightScreen
import nom.nom.nomsy.ui.screens.auth.OnboardingNameScreen
import nom.nom.nomsy.ui.screens.auth.OnboardingNutritionScreen
import nom.nom.nomsy.ui.screens.auth.OnboardingWeightScreen
import nom.nom.nomsy.ui.screens.auth.OnboardingWelcomeScreen
import nom.nom.nomsy.ui.screens.auth.RegisterScreen
import nom.nom.nomsy.ui.screens.auth.RegistrationCompleteScreen
import nom.nom.nomsy.ui.screens.profile.EditProfileScreen
import nom.nom.nomsy.ui.screens.profile.ProfileScreen
import nom.nom.nomsy.ui.screens.recipes.recipesScreen
import nom.nom.nomsy.viewModels.AuthViewModel
import nom.nom.nomsy.viewModels.AuthViewModelFactory
import nom.nom.nomsy.viewModels.FoodViewModel
import nom.nom.nomsy.viewModels.HomeViewModel
import nom.nom.nomsy.viewModels.ProfileViewModel
import nom.nom.nomsy.viewModels.ProfileViewModelFactory
import nom.nom.nomsy.viewModels.RecipeViewModel
import nom.nom.nomsy.viewModels.RecipeViewModelFactory

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
                    authViewModel = authViewModel,
                    foodViewModel = viewModel<FoodViewModel>()
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