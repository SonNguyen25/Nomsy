package com.example.nomsy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nomsy.ui.components.*
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.viewModels.AuthViewModel
import com.example.nomsy.viewModels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: HomeViewModel,
    onAddFoodClick: () -> Unit = { navController.navigate("add_food") }
) {
//    val uiState by viewModel.uiState.collectAsState()
//    val selectedDate by viewModel.selectedDate.collectAsState()
    val scrollState = rememberScrollState()

    // Hard-coded dummy data for demonstration

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
            // Date selector
//            val selectedDate =
//            DateSelector(
//                date = selectedDate,
//                onPreviousDay = { viewModel.decrementDate() },
//                onNextDay = { viewModel.incrementDate() }
//            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main content - using dummy data instead of state
            // Large calorie circle
            CalorieCircle(
                currentCalories = 20,
                goalCalories =  2000,
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
                    current =  20f,
                    goal =  50f,
                    name = "Protein",
                    modifier = Modifier.weight(1f)
                )

                // Carbs circle
                MacronutrientCircle(
                    current =  20f,
                    goal = 250f,
                    name = "Carbs",
                    modifier = Modifier.weight(1f)
                )

                // Fat circle
                MacronutrientCircle(
                    current = 20f,
                    goal = 70f,
                    name = "Fat",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Water intake with interactive capability
            WaterIntakeBar(
                currentIntake =  1f,
                goal =  2.5f,
                onWaterIntakeChange = { viewModel.updateWaterIntake(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Breakfast",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NomsyColors.Title
            )

//            for (food in dummyData.breakfastLogs) {
//                FoodItem(
//                    name = food.name,
//                    calories = food.calories,
//                    onDelete = { viewModel.deleteFoodLog(food.id) }
//                )
//            }

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Lunch",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NomsyColors.Title
            )

//            for (food in dummyData.lunchLogs) {
//                FoodItem(
//                    name = food.name,
//                    calories = food.calories,
//                    onDelete = { viewModel.deleteFoodLog(food.id) }
//                )
//            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dinner",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NomsyColors.Title
            )

//            for (food in dummyData.dinnerLogs) {
//                FoodItem(
//                    name = food.name,
//                    calories = food.calories,
//                    onDelete = { viewModel.deleteFoodLog(food.id) }
//                )
//            }

            // Add extra space at the bottom for the FAB
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Add food floating action button
        FloatingActionButton(
            onClick = onAddFoodClick,
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
}