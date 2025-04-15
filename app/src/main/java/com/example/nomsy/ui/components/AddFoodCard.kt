package com.example.nomsy.ui.components

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.ui.theme.NomsyColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nomsy.data.remote.AddMealRequest
import com.example.nomsy.viewModels.FoodViewModel
import kotlinx.coroutines.*
import com.example.nomsy.utils.Result

@Composable
fun addFoodCard(
    food: Food? = null,
    onDismiss: () -> Unit,
    onMealAdded: () -> Unit,
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


    val foodViewModel: FoodViewModel = viewModel()
    val foodDetail by foodViewModel.foodDetail.observeAsState()
    val date = LocalDate.now().toString().substring(0, 10)
    LaunchedEffect(Unit) {
        foodViewModel.fetchDailySummary(date)
    }
    val dailySummary by foodViewModel.dailySummary.observeAsState()

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

    val mealResult by foodViewModel.mealResult.observeAsState()

    LaunchedEffect(mealResult) {
        mealResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(context, "Meal successfully added!", Toast.LENGTH_SHORT).show()
                    foodViewModel.clearMealResult()
                    onMealAdded() // refresh home page
                    onDismiss()
                }
                is Result.Error -> {
                    Toast.makeText(context, "Failed to add meal.", Toast.LENGTH_SHORT).show()
                    foodViewModel.clearMealResult()
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
                            }
                        )

                    } else {
                        PictureCaptureForm()
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
                                    date = LocalDate.now().toString().substring(0, 10),
                                    meal_type = mealType.lowercase(),
                                    food_name = foodName,
                                    calories = foodData?.calories ?: calories.toIntOrNull() ?: 0,
                                    carbs = foodData?.carbs ?: carbs.toIntOrNull() ?: 0,
                                    protein = foodData?.protein ?: protein.toIntOrNull() ?: 0,
                                    fat = foodData?.fat ?: fat.toIntOrNull() ?: 0
                                )

                                foodViewModel.submitMeal(mealRequest)
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

@Composable
fun ManualInputForm(
    foodName: String, onFoodNameChange: (String) -> Unit,
    calories: String, onCaloriesChange: (String) -> Unit,
    protein: String, onProteinChange: (String) -> Unit,
    carbs: String, onCarbsChange: (String) -> Unit,
    fat: String, onFatChange: (String) -> Unit,
    mealType: String, onMealTypeChange: (String) -> Unit,
    calPercent: Float,
    proteinPercent: Float,
    carbsPercent: Float,
    fatPercent: Float,
    onSelectFood: (Food) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val foodViewModel: FoodViewModel = viewModel()

        LaunchedEffect(Unit) {
            if (foodViewModel.allFoods.isEmpty()) {
                foodViewModel.fetchAllFoods()
            }
        }

        SearchFoodDropdown(
            viewModel = foodViewModel,
            foodName = foodName,
            onSelectFood = onSelectFood,
            onQueryChange = onFoodNameChange
        )

        LabeledInputRow("Name:", foodName, onFoodNameChange)
        LabeledInputRow("Calories:", calories, onCaloriesChange, unit = "kcal", isNumeric = true)
        LabeledInputRow("Protein:", protein, onProteinChange, unit = "g", isNumeric = true)
        LabeledInputRow("Carbs:", carbs, onCarbsChange, unit = "g", isNumeric = true)
        LabeledInputRow("Fats:", fat, onFatChange, unit = "g", isNumeric = true)

        MealTypeSelector(
            selectedMealType = mealType,
            onMealTypeChange = onMealTypeChange
        )

        Text(
            "Daily Goals Completion",
            color = NomsyColors.Title,
            fontWeight = FontWeight.Bold,
            style = androidx.compose.material.MaterialTheme.typography.h6,
        )

        // Wrapped 2 per row
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutrientCircle("Calories", calPercent, NomsyColors.Title, modifier = Modifier.weight(1f))
                NutrientCircle("Protein", proteinPercent, NomsyColors.Title, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutrientCircle("Carbs", carbsPercent, NomsyColors.Title, modifier = Modifier.weight(1f))
                NutrientCircle("Fat", fatPercent, NomsyColors.Title, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun NutrientCircle(
    label: String,
    percent: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percent.coerceIn(0f, 100f) / 100f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "AnimatedPercentage"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Box(
        modifier = modifier
            .size(100.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10f
            val radius = size.minDimension / 2.2f

            drawCircle(
                color = NomsyColors.Subtitle.copy(alpha = 0.3f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            val sweepAngle = 360f * animatedPercentage.value
            drawArc(
                color = color,
                startAngle = 270f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(size.minDimension, size.minDimension),
                topLeft = Offset(
                    (size.width - size.minDimension) / 2f,
                    (size.height - size.minDimension) / 2f
                )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, color = NomsyColors.Texts)
            Text("${percent.toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NomsyColors.Texts)
        }
    }
}

@Composable
fun SearchFoodDropdown(
    viewModel: FoodViewModel,
    foodName: String,
    onSelectFood: (Food) -> Unit,
    onQueryChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = viewModel.searchResults
    val focusManager = LocalFocusManager.current

    var searchJob by remember { mutableStateOf<Job?>(null) }

    val coroutineScope = rememberCoroutineScope()


    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                onQueryChange(it)
                expanded = true

                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    delay(300)
                    viewModel.searchFoodsFromApi(it)
                }
            },
            placeholder = {
                Text("Search Food", color = NomsyColors.Texts.copy(alpha = 0.6f))
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp)),
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            textStyle = TextStyle(color = NomsyColors.Texts),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = NomsyColors.Texts,
                unfocusedBorderColor = NomsyColors.Texts,
                textColor = NomsyColors.Texts,
                cursorColor = NomsyColors.Texts,
                placeholderColor = NomsyColors.Texts.copy(alpha = 0.6f)
            ),
            trailingIcon = {
                Row {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onQueryChange("")
                            expanded = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = NomsyColors.Subtitle)
                        }
                    }
                    IconButton(onClick = { focusManager.clearFocus() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = NomsyColors.Subtitle)
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    expanded = true
                }
            )
        )

        DropdownMenu(
            expanded = expanded && searchResults.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(300.dp)
                .background(NomsyColors.Background)
        ) {
            searchResults.take(5).forEach { food ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .border(1.dp, NomsyColors.Title, RoundedCornerShape(8.dp))
                        .clickable {
                            onSelectFood(food)
                            searchQuery = food.food_name
                            expanded = false
                            focusManager.clearFocus()
                        }
                        .padding(12.dp)
                ) {
                    Text(food.food_name, color = NomsyColors.Texts)
                }
            }
        }
    }
}

@Composable
fun LabeledInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String? = null,
    isNumeric: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            modifier = Modifier.width(80.dp),
            color = NomsyColors.Texts
        )

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (!isNumeric || newValue.all { it.isDigit() }) {
                    onValueChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Text
            ),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .padding(end = if (unit != null) 8.dp else 0.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = NomsyColors.Texts,
                cursorColor = NomsyColors.Title,
                focusedBorderColor = NomsyColors.Title,
                unfocusedBorderColor = NomsyColors.Subtitle,
                backgroundColor = NomsyColors.PictureBackground
            )
        )

        if (unit != null) {
            Text(
                text = unit,
                color = NomsyColors.Texts,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(32.dp)
            )
        }
    }
}



@Composable
fun MealTypeSelector(
    selectedMealType: String,
    onMealTypeChange: (String) -> Unit
) {
    val mealOptions = listOf("Breakfast", "Lunch", "Dinner")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        mealOptions.forEach { option ->
            val isSelected = selectedMealType == option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) NomsyColors.Title else NomsyColors.Subtitle,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        color = if (isSelected) NomsyColors.Title else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onMealTypeChange(option) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) NomsyColors.Background else NomsyColors.Subtitle,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Composable
fun PictureCaptureForm() {
    val context = LocalContext.current
    val foodViewModel: FoodViewModel = viewModel()
    val recognizedFood by foodViewModel.recognizedFood.observeAsState("")
    val foodDetail by foodViewModel.foodDetail.observeAsState()

    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        imageBitmap = bitmap
        bitmap?.let {
            foodViewModel.analyzeWithSpoonacular(it)
        }
    }

    val boxSize = 500.dp
    val cornerRadius = 8.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .clip(RoundedCornerShape(cornerRadius))
                .background(NomsyColors.PictureBackground)
                .border(1.dp, NomsyColors.Title, RoundedCornerShape(cornerRadius)),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(cornerRadius)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("Capture to Analyze Calories!", color = NomsyColors.Subtitle)
            }
        }

        Button(
            onClick = { launcher.launch(null) },
            colors = ButtonDefaults.buttonColors(containerColor = NomsyColors.Title)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Take Picture"
            )
        }

        if (recognizedFood.isNotBlank()) {
            Text(
                text = "Detected: $recognizedFood",
                color = NomsyColors.Title,
                modifier = Modifier.padding(8.dp)
            )
        }

        foodDetail?.let {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Food: ${it.food_name}", color = NomsyColors.Texts)
                Text("Calories: ${it.calories} kcal", color = NomsyColors.Texts)
                Text("Carbs: ${it.carbs} g", color = NomsyColors.Texts)
                Text("Protein: ${it.protein} g", color = NomsyColors.Texts)
                Text("Fat: ${it.fat} g", color = NomsyColors.Texts)
            }
        }
    }
}
