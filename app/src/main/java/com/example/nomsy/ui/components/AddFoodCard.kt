package com.example.nomsy.ui.components

import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.window.DialogProperties
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.ui.theme.NomsyColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun addFoodCard(
    food: Food? = null,
    onDismiss: () -> Unit
) {
    var inputMethod by remember { mutableStateOf("Manual") }
    val context = LocalContext.current

    val dailyGoals = mapOf(
        "calories" to 2000f,
        "protein" to 50f,
        "carbs" to 250f,
        "fat" to 70f
    )

    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    val mealTypeState = remember { mutableStateOf("Lunch") }
    val mealType = mealTypeState.value
    val setMealType: (String) -> Unit = { mealTypeState.value = it }

    //Calculate the percentages
    val calPercent = (calories.toFloatOrNull() ?: 0f) / dailyGoals["calories"]!! * 100
    val proteinPercent = (protein.toFloatOrNull() ?: 0f) / dailyGoals["protein"]!! * 100
    val carbsPercent = (carbs.toFloatOrNull() ?: 0f) / dailyGoals["carbs"]!! * 100
    val fatPercent = (fat.toFloatOrNull() ?: 0f) / dailyGoals["fat"]!! * 100


    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(700.dp)
                .height(800.dp)
                .border(1.dp, NomsyColors.Title, shape = RoundedCornerShape(8.dp))
                .background(NomsyColors.Background, shape = RoundedCornerShape(16.dp))
        ) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Toggle
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

                Spacer(modifier = Modifier.height(16.dp))

                if (inputMethod == "Manual") {
                    ManualInputForm(
                        foodName, { foodName = it },
                        calories, { calories = it },
                        carbs, { carbs = it },
                        protein, { protein = it },
                        fat, { fat = it },
                        mealType, setMealType,
                        calPercent,
                        proteinPercent,
                        carbsPercent,
                        fatPercent
                    )

                } else {
                    PictureCaptureSection()
                }
            }

            // Sticky footer
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(NomsyColors.Background)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val json = JSONObject().apply {
                                    put("date", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                                    put("meal_type", mealType.lowercase())
                                    put("food_name", foodName)
                                    put("calories", calories.toIntOrNull() ?: 0)
                                    put("carbs", carbs.toIntOrNull() ?: 0)
                                    put("protein", protein.toIntOrNull() ?: 0)
                                    put("fat", fat.toIntOrNull() ?: 0)
                                }

                                val client = OkHttpClient()
                                val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                                val request = Request.Builder()
                                    .url("https://sonnguyen25.pythonanywhere.com/meals")
                                    .post(body)
                                    .build()

                                try {
                                    val response = client.newCall(request).execute()
                                    if (response.isSuccessful) {
                                        withContext(Dispatchers.Main) {
                                            onDismiss()
                                        }
                                        Log.e("AddMeal", "Meal Added Successfully")
                                    } else {
                                        Log.e("AddMeal", "Error: ${response.body?.string()}")
                                    }
                                } catch (e: Exception) {
                                    Log.e("AddMeal", "Exception: ${e.message}")
                                }
                            }
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
                        border = BorderStroke(1.dp, NomsyColors.Title)) {
                        Text("Close", color = NomsyColors.Subtitle)
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
    fatPercent: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        LabeledInputRow("Name:", foodName, onFoodNameChange)
        LabeledInputRow("Calories:", calories, onCaloriesChange, unit = "kcal")
        LabeledInputRow("Protein:", protein, onProteinChange, unit = "g")
        LabeledInputRow("Carbs:", carbs, onCarbsChange, unit = "g")
        LabeledInputRow("Fats:", fat, onFatChange, unit = "g")

        MealTypeSelector(
            selectedMealType = mealType,
            onMealTypeChange = onMealTypeChange
        )

        Text("Daily Goals Completion", color = NomsyColors.Subtitle)

        NutrientProgressBar("Calories", calPercent, NomsyColors.Title)
        NutrientProgressBar("Protein", proteinPercent, NomsyColors.Title)
        NutrientProgressBar("Carbs", carbsPercent, NomsyColors.Title)
        NutrientProgressBar("Fat", fatPercent, NomsyColors.Title)
    }
}

@Composable
fun NutrientProgressBar(label: String, percent: Float, color: Color) {
    Column {
        LinearProgressIndicator(
            progress = (percent / 100f).coerceIn(0f, 1f),
            color = color,
            trackColor = NomsyColors.Subtitle.copy(alpha = 0.2f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, fontSize = 12.sp, color = NomsyColors.Texts)
            Text("${percent.toInt()}%", fontSize = 12.sp, color = NomsyColors.Texts)
        }
    }
}


@Composable
fun LabeledInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String? = null
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
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .width(10.dp)
                .padding(end = if (unit != null) 8.dp else 0.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = NomsyColors.Texts,
                cursorColor = NomsyColors.Title,
                focusedBorderColor = NomsyColors.Title,
                unfocusedBorderColor = NomsyColors.Subtitle,
                focusedLabelColor = NomsyColors.Title,
                unfocusedLabelColor = NomsyColors.Subtitle,
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
fun PictureCaptureSection() {
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        imageBitmap = bitmap
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
                .background(Color.LightGray)
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
            colors = ButtonDefaults.buttonColors(
                containerColor = NomsyColors.Title
            ),) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Take Picture"
            )
        }
    }
}
