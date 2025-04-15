package com.example.nomsy.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomsy.data.local.models.User
import com.example.nomsy.ui.components.OnboardingBaseScreen
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.viewModels.IAuthViewModel

@Composable
fun OnboardingNutritionScreen(
    navController: NavController,
    authViewModel: IAuthViewModel = viewModel()
) {
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }

    // Get user details for calculation
    val userAge = authViewModel.getUserAge()
    val userWeight = authViewModel.getUserWeight()
    val userHeight = authViewModel.getUserHeight()
    val userFitnessGoal = authViewModel.getUserFitnessGoal()
    val context = LocalContext.current

    OnboardingBaseScreen(
        content = {
            Text(
                text = "What are your nutrition goals?",
                color = NomsyColors.Title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Calories Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = "Calories:",
                    color = NomsyColors.Texts,
                    fontSize = 16.sp,
                    modifier = Modifier.width(100.dp)
                )

                OutlinedTextField(
                    value = calories,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            calories = input
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = NomsyColors.Texts,
                        cursorColor = NomsyColors.Texts,
                        focusedBorderColor = NomsyColors.Title,
                        unfocusedBorderColor = NomsyColors.Subtitle,
                        backgroundColor = NomsyColors.PictureBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Protein Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = "Protein:",
                    color = NomsyColors.Texts,
                    fontSize = 16.sp,
                    modifier = Modifier.width(100.dp)
                )

                OutlinedTextField(
                    value = protein,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            protein = input
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = NomsyColors.Texts,
                        cursorColor = NomsyColors.Texts,
                        focusedBorderColor = NomsyColors.Title,
                        unfocusedBorderColor = NomsyColors.Subtitle,
                        backgroundColor = NomsyColors.PictureBackground
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Text("g", color = NomsyColors.Subtitle) }

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Carbs Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = "Carbs:",
                    color = NomsyColors.Texts,
                    fontSize = 16.sp,
                    modifier = Modifier.width(100.dp)
                )

                OutlinedTextField(
                    value = carbs,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            carbs = input
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = NomsyColors.Texts,
                        cursorColor = NomsyColors.Texts,
                        focusedBorderColor = NomsyColors.Title,
                        unfocusedBorderColor = NomsyColors.Subtitle,
                        backgroundColor = NomsyColors.PictureBackground
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Text("g", color = NomsyColors.Subtitle) }

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fat Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = "Fat:",
                    color = NomsyColors.Texts,
                    fontSize = 16.sp,
                    modifier = Modifier.width(100.dp)
                )

                OutlinedTextField(
                    value = fat,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            fat = input
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = NomsyColors.Texts,
                        cursorColor = NomsyColors.Texts,
                        focusedBorderColor = NomsyColors.Title,
                        unfocusedBorderColor = NomsyColors.Subtitle,
                        backgroundColor = NomsyColors.PictureBackground
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Text("g", color = NomsyColors.Subtitle) }

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Water Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = "Water:",
                    color = NomsyColors.Texts,
                    fontSize = 16.sp,
                    modifier = Modifier.width(100.dp)
                )

                OutlinedTextField(
                    value = water,
                    onValueChange = { input ->
                        // Allow digits and a single decimal point
                        if (input.isEmpty() ||
                            (input.count { it == '.' } <= 1 &&
                                    input.all { it.isDigit() || it == '.' })
                        ) {
                            water = input
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = NomsyColors.Texts,
                        cursorColor = NomsyColors.Texts,
                        focusedBorderColor = NomsyColors.Title,
                        unfocusedBorderColor = NomsyColors.Subtitle,
                        backgroundColor = NomsyColors.PictureBackground
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Text("L", color = NomsyColors.Subtitle) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Calculate nutrition goals based on user details
                    val basalMetabolicRate = when {
                        userAge > 0 && userWeight > 0 && userHeight > 0 -> {
                            // Basic BMR calculation (simplified)
                            val bmr = 10 * userWeight + 6.25 * userHeight - 5 * userAge + 5

                            // Adjust based on fitness goal
                            when (userFitnessGoal) {
                                "cut" -> (bmr * 0.8).toInt()
                                "maintain" -> bmr.toInt()
                                "bulk" -> (bmr * 1.2).toInt()
                                else -> bmr.toInt()
                            }
                        }

                        else -> {
                            // Default values if data is missing
                            2000
                        }
                    }

                    // Set calculated values
                    calories = basalMetabolicRate.toString()
                    protein =
                        (userWeight * 1.8).toInt().toString() // 1.8g protein per kg of body weight
                    carbs = (basalMetabolicRate * 0.4 / 4).toInt()
                        .toString() // 40% of calories from carbs
                    fat = (basalMetabolicRate * 0.3 / 9).toInt()
                        .toString() // 30% of calories from fat
                    // Calculate water intake: ~30-35ml per kg of body weight
                    // Convert ml to liters
                    val waterIntakeL = ((userWeight * 33) / 1000.0).toInt()
                    water = waterIntakeL.toString()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = NomsyColors.PictureBackground,
                    contentColor = NomsyColors.Title
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Calculate for me")
            }
        },
        buttonText = "Next",
        onNextClick = {
            when {
                calories.isEmpty() -> {
                    Toast.makeText(context, "Please enter calories", Toast.LENGTH_SHORT).show()
                }

                calories.toIntOrNull() == null -> {
                    Toast.makeText(
                        context,
                        "Please enter a valid number for calories",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                protein.isEmpty() -> {
                    Toast.makeText(context, "Please enter protein", Toast.LENGTH_SHORT).show()
                }

                protein.toIntOrNull() == null -> {
                    Toast.makeText(
                        context,
                        "Please enter a valid number for protein",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                carbs.isEmpty() -> {
                    Toast.makeText(context, "Please enter carbs", Toast.LENGTH_SHORT).show()
                }

                carbs.toIntOrNull() == null -> {
                    Toast.makeText(
                        context,
                        "Please enter a valid number for carbs",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                fat.isEmpty() -> {
                    Toast.makeText(context, "Please enter fat", Toast.LENGTH_SHORT).show()
                }

                fat.toIntOrNull() == null -> {
                    Toast.makeText(
                        context,
                        "Please enter a valid number for fat",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                water.isEmpty() -> {
                    Toast.makeText(context, "Please enter water", Toast.LENGTH_SHORT).show()
                }

                water.toIntOrNull() == null -> {
                    Toast.makeText(
                        context,
                        "Please enter a valid number for water",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    // All inputs are non-empty and valid integers
                    val nutritionGoals = mapOf(
                        "calories" to calories.toInt(),
                        "protein" to protein.toInt(),
                        "carbs" to carbs.toInt(),
                        "fat" to fat.toInt(),
                        "water" to water.toInt()
                    )

                    authViewModel.setUserNutritionGoals(nutritionGoals)

                    val user = User(
                        id = "",
                        username = authViewModel.getUsername(),
                        password = authViewModel.getPassword(),
                        name = authViewModel.getUserName(),
                        age = authViewModel.getUserAge(),
                        height = authViewModel.getUserHeight(),
                        weight = authViewModel.getUserWeight(),
                        fitness_goal = authViewModel.getUserFitnessGoal(),
                        nutrition_goals = nutritionGoals
                    )

                    authViewModel.register(user)
                    navController.navigate("registration_complete") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true

                    }
                }
            }
        }
    )
}
