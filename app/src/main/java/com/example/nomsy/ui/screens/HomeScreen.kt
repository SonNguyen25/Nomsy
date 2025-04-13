package com.example.nomsy.ui.screens

import android.os.Build
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.ui.components.*
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.AuthViewModel
import com.example.nomsy.viewModels.HomeViewModel
import com.example.nomsy.viewModels.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
) {
//    val uiState by viewModel.uiState.collectAsState()
//    val selectedDate by viewModel.selectedDate.collectAsState()
    val scrollState = rememberScrollState()
    val date = viewModel.selectedDate.collectAsState().value
    val profileResult by profileViewModel.profile.observeAsState()
    var (waterGoal, calorieGoal, proteinGoal, carbsGoal, fatGoal) = List(5) { 0 }
    var showAddFoodDialog by remember { mutableStateOf(false) }


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

            // Date selector
            DateSelector(
                date = date.toString(),
                onPreviousDay = { viewModel.decrementDate() },
                onNextDay = { viewModel.incrementDate() }
            )


            Spacer(modifier = Modifier.height(16.dp))


            //loading profile material
            when (profileResult) {
                is Result.Loading -> {
                    androidx.compose.material.CircularProgressIndicator(
                        color = NomsyColors.Title
                    )
                }

                is Result.Error -> {
                    androidx.compose.material.Text(
                        text = "Error loading profile",
                        color = MaterialTheme.colors.error,

                        )
                }

                is Result.Success -> {
                    val user = (profileResult as Result.Success<User>).data
                    waterGoal = user.nutrition_goals["water"] ?: 2
                    calorieGoal = user.nutrition_goals["calories"] ?: 2000
                    proteinGoal = user.nutrition_goals["protein"] ?: 50
                    carbsGoal = user.nutrition_goals["carbs"] ?: 250
                    fatGoal = user.nutrition_goals["fat"] ?: 70
                    // load nutrition progress circles and water intake guages
                    // Large calorie circle
                    CalorieCircle(
                        currentCalories = 1567,
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
                            current = 20f,
                            goal = proteinGoal.toFloat(),
                            name = "Protein",
                            modifier = Modifier.weight(1f)
                        )

                        // Carbs circle
                        MacronutrientCircle(
                            current = 20f,
                            goal = carbsGoal.toFloat(),
                            name = "Carbs",
                            modifier = Modifier.weight(1f)
                        )

                        // Fat circle
                        MacronutrientCircle(
                            current = 20f,
                            goal = fatGoal.toFloat(),
                            name = "Fat",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Water intake with interactive capability
                    WaterIntakeBar(
                        currentIntake = 1f,
                        goal = waterGoal.toFloat(),
                        onWaterIntakeChange = { viewModel.updateWaterIntake(it) }
                    )


                }

                null -> {
                    // initial state before load
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Meal sections with fixed data for prototype
            val breakfastMeals = remember {
                listOf(
                    MealItem("Scrambled Eggs", 220, 2, 14, 16),
                    MealItem("Whole Wheat Toast", 90, 15, 3, 1)
                )
            }
            val lunchMeals = remember {
                listOf(
                    MealItem("Grilled Chicken Salad", 350, 10, 32, 18)
                )
            }
            val dinnerMeals = remember {
                listOf(
                    MealItem("Salmon Fillet", 280, 0, 22, 18),
                    MealItem("Steamed Broccoli", 55, 10, 4, 0),
                    MealItem("Brown Rice", 120, 25, 3, 1)
                )
            }

            MealListSection(
                title = "Breakfast",
                meals = breakfastMeals
            )
            Spacer(modifier = Modifier.height(16.dp))
            MealListSection(
                title = "Lunch",
                meals = lunchMeals
            )
            Spacer(modifier = Modifier.height(16.dp))
            MealListSection(
                title = "Dinner",
                meals = dinnerMeals
            )

        }
        Spacer(Modifier.height(32.dp))
        Spacer(Modifier.height(32.dp))

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

