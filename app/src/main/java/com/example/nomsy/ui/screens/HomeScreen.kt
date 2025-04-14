package com.example.nomsy.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.models.User
import com.example.nomsy.ui.components.*
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.AuthViewModel
import com.example.nomsy.viewModels.HomeViewModel
import com.example.nomsy.viewModels.ProfileViewModel
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(),
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel(),
) {
    // FETCHES PROFILE DATA
    val username = authViewModel.getCurrentUsername() // Get the username
    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            profileViewModel.fetchByUsername(username)
        }
    }

    val scrollState = rememberScrollState()
    val date = viewModel.selectedDate.collectAsState().value
    val profileResult by profileViewModel.profile.observeAsState()
    val nutritionResult by viewModel.nutritionTotals.observeAsState(initial = Result.Loading)
    val mealsResult by viewModel.mealsByType.observeAsState(initial = Result.Loading)
    val waterIntake by viewModel.waterIntake.collectAsState()

    var (waterGoal, calorieGoal, proteinGoal, carbsGoal, fatGoal) = List(5) { 0 }
    var showAddFoodDialog by remember { mutableStateOf(false) }

    // nutrition goals early
    when (profileResult) {
        is Result.Success -> {
            val user = (profileResult as Result.Success<User>).data
            waterGoal = user.nutrition_goals["water"] ?: 2
            calorieGoal = user.nutrition_goals["calories"] ?: 2000
            proteinGoal = user.nutrition_goals["protein"] ?: 50
            carbsGoal = user.nutrition_goals["carbs"] ?: 250
            fatGoal = user.nutrition_goals["fat"] ?: 70
        }

        is Result.Error -> {
            androidx.compose.material.Text(
                text = "Error loading profile",
                color = MaterialTheme.colors.error,
            )
        }

        Result.Loading -> {
            androidx.compose.material.CircularProgressIndicator(
                color = NomsyColors.Title
            )
        }

        null -> {}
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NomsyColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {


            Spacer(modifier = Modifier.height(24.dp))

            // loading current nutrition totals material
            when (nutritionResult) {
                is Result.Success -> {
                    // Date selector
                    DateSelector(
                        date = date.toString(),
                        onPreviousDay = { viewModel.decrementDate() },
                        onNextDay = { viewModel.incrementDate() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val nutrition = (nutritionResult as Result.Success<DailySummaryEntity?>).data
                    // load nutrition progress circles and water intake gauges
                    // Large calorie circle
                    CalorieCircle(
                        currentCalories = (nutrition?.totalCalories ?: 69),
                        goalCalories = calorieGoal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Row of macro circles (protein, carbs, fat)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Protein circle

                        MacronutrientCircle(
                            current = (nutrition?.totalProtein?.toFloat() ?: 69f),
                            goal = proteinGoal.toFloat(),
                            name = "Protein",
                            modifier = Modifier.weight(1f)
                        )


                        // Carbs circle
                        MacronutrientCircle(
                            current = (nutrition?.totalCarbs?.toFloat() ?: 69f),
                            goal = carbsGoal.toFloat(),
                            name = "Carbs",
                            modifier = Modifier.weight(1f)
                        )

                        // Fat circle
                        MacronutrientCircle(
                            current = (nutrition?.totalFat?.toFloat() ?: 69f),
                            goal = fatGoal.toFloat(),
                            name = "Fat",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))


                    WaterIntakeBar(
                        currentIntake = waterIntake.toFloat(),
                        goal = waterGoal.toFloat(),
                        onWaterIntakeChange = {
                            viewModel.updateWaterIntake(
                                "2025-04-$date",
                                newWaterIntake = it.toDouble()
                            )
                        }
                    )
                }

                is Result.Loading -> {
                    androidx.compose.material.CircularProgressIndicator(
                        color = NomsyColors.Title,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)

                    )
                }

                is Result.Error -> {
                    androidx.compose.material.Text(
                        text = "Error loading profile",
                        color = MaterialTheme.colors.error,

                        )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


//            // Meal sections with fixed data for prototype
//            val breakfastMeals = remember {
//                listOf(
//                    MealItem("Scrambled Eggs", 220, 2, 14, 16),
//                    MealItem("Whole Wheat Toast", 90, 15, 3, 1)
//                )
//            }
//            val lunchMeals = remember {
//                listOf(
//                    MealItem("Grilled Chicken Salad", 350, 10, 32, 18)
//                )
//            }
//            val dinnerMeals = remember {
//                listOf(
//                    MealItem("Salmon Fillet", 280, 0, 22, 18),
//                    MealItem("Steamed Broccoli", 55, 10, 4, 0),
//                    MealItem("Brown Rice", 120, 25, 3, 1)
//                )
//            }
//
//            MealListSection(
//                title = "Breakfast",
//                meals = breakfastMeals
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            MealListSection(
//                title = "Lunch",
//                meals = lunchMeals
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            MealListSection(
//                title = "Dinner",
//                meals = dinnerMeals
//            )
//            Spacer(modifier = Modifier.height(50.dp))

            // Display meals by meal type
            when (mealsResult) {
                is Result.Loading -> {
                    androidx.compose.material.CircularProgressIndicator(
                        color = NomsyColors.Title,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }

                is Result.Error -> {
                    Text(
                        text = "error has occured",
                        color = NomsyColors.Subtitle
                    )
                }

                is Result.Success -> {
                    val mealsByType = (mealsResult as Result.Success).data
                    if (mealsByType.isEmpty()) {
                        Text(
                            text = "No meals logged for this date",
                            color = NomsyColors.Subtitle
                        )
                    } else {
                        // Display each meal type section
                        mealsByType.forEach { (mealType, meals) ->
                            // Capitalize first letter for display
                            val displayMealType = mealType.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                            }

                            MealListSection(
                                title = displayMealType,
                                meals = meals,
                                onDelete = { meal ->
                                    Log.d("HomeScreen", "Delete requested for: ${meal.food_name}")
                                    viewModel.deleteMeal("2025-04-$date", meal.food_name)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                else -> {
                    // Fallback case
                    Text(
                        text = "Loading meal data...",
                        color = NomsyColors.Subtitle
                    )
                }
            }
            Spacer(Modifier.height(50.dp))
        }


        // Add food floating action button
        FloatingActionButton(
            onClick = { showAddFoodDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = NomsyColors.Title,
            contentColor = NomsyColors.Background
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Food"
            )
        }
    }

    if (showAddFoodDialog) {
        addFoodCard(
            onDismiss = { showAddFoodDialog = false }
        )
    }
}

