package com.example.nomsy.ui.components

import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun addFoodCard(
    food: Food? = null,
    onDismiss: () -> Unit
) {
    var inputMethod by remember { mutableStateOf("Manual") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Manual", "Picture").forEach { method ->
                    Text(
                        text = method,
                        color = if (inputMethod == method) NomsyColors.Title else NomsyColors.Subtitle,
                        modifier = Modifier
                            .clickable { inputMethod = method }
                            .padding(8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            var foodName by remember { mutableStateOf("") }
            var calories by remember { mutableStateOf("") }
            var carbs by remember { mutableStateOf("") }
            var protein by remember { mutableStateOf("") }
            var fat by remember { mutableStateOf("") }
            var mealType by remember { mutableStateOf("Lunch") }

            when (inputMethod) {
                "Manual" -> ManualInputForm(
                    foodName, { foodName = it },
                    calories, { calories = it },
                    carbs, { carbs = it },
                    protein, { protein = it },
                    fat, { fat = it },
                    mealType, { mealType = it }
                )
                "Picture" -> PictureCaptureSection()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
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
                        .url("https://your-api-url.com/meals") // TODO: replace with your actual base URL
                        .post(body)
                        .build()

                    try {
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                onDismiss()
                            }
                        } else {
                            Log.e("AddMeal", "Error: ${response.body?.string()}")
                        }
                    } catch (e: Exception) {
                        Log.e("AddMeal", "Exception: ${e.message}")
                    }
                }
            }) {
                Text("Submit")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    }
}

@Composable
fun ManualInputForm(
    foodName: String, onFoodNameChange: (String) -> Unit,
    calories: String, onCaloriesChange: (String) -> Unit,
    carbs: String, onCarbsChange: (String) -> Unit,
    protein: String, onProteinChange: (String) -> Unit,
    fat: String, onFatChange: (String) -> Unit,
    mealType: String, onMealTypeChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = foodName, onValueChange = onFoodNameChange, label = { Text("Food Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = calories, onValueChange = onCaloriesChange, label = { Text("Calories") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = carbs, onValueChange = onCarbsChange, label = { Text("Carbs") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = protein, onValueChange = onProteinChange, label = { Text("Protein") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fat, onValueChange = onFatChange, label = { Text("Fat") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = mealType, onValueChange = onMealTypeChange, label = { Text("Meal Type") }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun PictureCaptureSection() {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        // handle image recognition here
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { launcher.launch(null) }) {
            Text("Take a Picture")
        }
    }
}
