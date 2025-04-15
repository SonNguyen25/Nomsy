package com.example.nomsy.ui.components

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nomsy.data.local.entities.Food
import com.example.nomsy.ui.theme.NomsyColors
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.livedata.observeAsState
import com.example.nomsy.data.remote.AddMealRequest
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.IFoodViewModel

@Composable
fun addFoodCard(
    date: String,
    food: Food? = null,
    onDismiss: () -> Unit,
    onMealAdded: () -> Unit,
    viewModel: IFoodViewModel
) {
    var inputMethod by remember { mutableStateOf("Manual") }
    val context = LocalContext.current

    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    val mealTypeState = remember { mutableStateOf("Lunch") }
    val mealType = mealTypeState.value
    val setMealType: (String) -> Unit = { mealTypeState.value = it }


    val foodDetail by viewModel.foodDetail.observeAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchDailySummary(date)
    }
    val dailySummary by viewModel.dailySummary.observeAsState()

    val dailyGoals = mapOf(
        "calories" to (dailySummary?.calories?.toFloat() ?: 2000f),
        "protein" to (dailySummary?.protein?.toFloat() ?: 250f),
        "carbs" to (dailySummary?.carbs?.toFloat() ?: 250f),
        "fat" to (dailySummary?.fat?.toFloat() ?: 70f)
    )


    //Calculate the percentages
    val caloriesValue = calories.toFloatOrNull() ?: 0f
    val proteinValue = protein.toFloatOrNull() ?: 0f
    val carbsValue = carbs.toFloatOrNull() ?: 0f
    val fatValue = fat.toFloatOrNull() ?: 0f

    val calGoal = dailyGoals["calories"] ?: 0f
    val proteinGoal = dailyGoals["protein"] ?: 0f
    val carbsGoal = dailyGoals["carbs"] ?: 0f
    val fatGoal = dailyGoals["fat"] ?: 0f

    // So that when the goal is fetched as 0, calculation can still be done
    fun safePercentage(numerator: Float, denominator: Float): Float {
        return if (denominator > 0f && !numerator.isNaN() && !denominator.isNaN()) {
            (numerator / denominator) * 100
        } else {
            0f
        }
    }

    val calPercent = safePercentage(caloriesValue, if (calGoal > 0f) calGoal else caloriesValue + calGoal)
    val proteinPercent = safePercentage(proteinValue, if (proteinGoal > 0f) proteinGoal else proteinValue + proteinGoal)
    val carbsPercent = safePercentage(carbsValue, if (carbsGoal > 0f) carbsGoal else carbsValue + carbsGoal)
    val fatPercent = safePercentage(fatValue, if (fatGoal > 0f) fatGoal else fatValue + fatGoal)

    LaunchedEffect(foodDetail) {
        foodDetail?.let {
            foodName = it.food_name
            calories = it.calories.toString()
            protein = it.protein.toString()
            carbs = it.carbs.toString()
            fat = it.fat.toString()
        }
    }

    val mealResult by viewModel.mealResult.observeAsState()

    LaunchedEffect(mealResult) {
        mealResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(context, "Meal successfully added!", Toast.LENGTH_SHORT).show()
                    viewModel.clearMealResult()
                    onMealAdded() // refresh home page
                    onDismiss()
                }
                is Result.Error -> {
                    Toast.makeText(context, "Failed to add meal.", Toast.LENGTH_SHORT).show()
                    viewModel.clearMealResult()
                }
                is Result.Loading -> {}
            }
        }
    }


    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(700.dp)
                .height(800.dp)
                .border(1.dp, NomsyColors.Title, shape = RoundedCornerShape(8.dp))
                .background(NomsyColors.Background, shape = RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Static black toggle header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Manual", "Picture").forEach { method ->
                            val isSelected = inputMethod == method
                            val underlineWidth by animateDpAsState(
                                targetValue = if (isSelected) 70.dp else 0.dp,
                                label = "Underline Animation"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { inputMethod = method }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = method,
                                    color = if (isSelected) NomsyColors.Title else NomsyColors.Subtitle,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Box(
                                    modifier = Modifier
                                        .height(2.dp)
                                        .width(underlineWidth)
                                        .background(NomsyColors.Title)
                                )
                            }
                        }
                    }
                }

                // Scrollable form content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (inputMethod == "Manual") {
                        ManualInputForm(
                            foodName, { foodName = it },
                            calories, { calories = it },
                            protein, { protein = it },
                            carbs, { carbs = it },
                            fat, { fat = it },
                            mealType, setMealType,
                            calPercent, proteinPercent, carbsPercent, fatPercent,
                            onSelectFood = {
                                foodName = it.food_name
                                calories = it.calories.toString()
                                protein = it.protein.toString()
                                carbs = it.carbs.toString()
                                fat = it.fat.toString()
                            },
                            viewModel = viewModel
                        )

                    } else {
                        PictureCaptureForm(viewModel = viewModel)
                    }
                }

                // Sticky footer (shorter)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NomsyColors.Background)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                val foodData = if (inputMethod == "Picture") foodDetail else null

                                val mealRequest = AddMealRequest(
                                    // This is put here to demo and simplify
                                    date = date,
                                    meal_type = mealType.lowercase(),
                                    food_name = foodName,
                                    calories = foodData?.calories ?: calories.toIntOrNull() ?: 0,
                                    carbs = foodData?.carbs ?: carbs.toIntOrNull() ?: 0,
                                    protein = foodData?.protein ?: protein.toIntOrNull() ?: 0,
                                    fat = foodData?.fat ?: fat.toIntOrNull() ?: 0
                                )

                                viewModel.submitMeal(mealRequest)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NomsyColors.Background
                            ),
                            border = BorderStroke(1.dp, NomsyColors.Title)
                        ) {
                            Text("Submit")
                        }

                        TextButton(
                            onClick = onDismiss,
                            border = BorderStroke(1.dp, NomsyColors.Title)
                        ) {
                            Text("Close", color = NomsyColors.Subtitle)
                        }
                    }
                }
            }
        }
    }
}

