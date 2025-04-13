package com.example.nomsy.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomsy.ui.components.addFoodCard
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.viewModels.FoodViewModel
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun AddFoodScreen(
//    navController: NavController,
//    foodViewModel: FoodViewModel
//) {
//    addFoodCard(
//        onDismiss = {
//            navController.popBackStack()
//        }
//    )
//}

//    val context = LocalContext.current
//    var query by remember { mutableStateOf("") }
//    var name by remember { mutableStateOf("") }
//    var calories by remember { mutableStateOf("") }
//    var protein by remember { mutableStateOf("") }
//    var carbs by remember { mutableStateOf("") }
//    var fats by remember { mutableStateOf("") }
//
//    var selectedMeal by remember { mutableStateOf("Breakfast") }
//    val meals = listOf("Breakfast", "Lunch", "Dinner")
//
//    // ML‑Kit recognized name
//    val recognizedFood by foodViewModel.recognizedFood.observeAsState("")
//
//    // API‑looked up Food detail
//    val foodDetail by foodViewModel.foodDetail.observeAsState()
//
//    // Launcher to take a picture
//    val takePictureLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicturePreview()
//    ) { bitmap ->
//        bitmap?.let {
//            foodViewModel.processFoodImage(context, it)
//        }
//    }
//
//    // Launcher to request CAMERA permission
//    val cameraPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { granted ->
//        if (granted) {
//            takePictureLauncher.launch(null)
//        } else {
//            Toast.makeText(context, "Camera permission is required to scan food", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // When ML Kit gives us a name, auto‑fill the search query & fire the API search
//    LaunchedEffect(recognizedFood) {
//        if (recognizedFood.isNotBlank()) {
//            query = recognizedFood
//            foodViewModel.searchFood(recognizedFood)
//        }
//    }
//
//    // When the API returns nutrition info, fill the form
//    LaunchedEffect(foodDetail) {
//        foodDetail?.let { f ->
//            name = f.food_name
//            calories = f.calories.toString()
//            protein = f.protein.toString()
//            carbs = f.carbs.toString()
//            fats = f.fat.toString()
//        }
//    }
//
//    // dummy percentages for daily goals
//    val percentCalories = 0.13f
//    val percentCarbs    = 0.21f
//    val percentFat      = 0.0f
//    val percentProtein  = 0.13f
//
//    Column(
//        Modifier
//            .fillMaxSize()
//            .background(NomsyColors.Background)
//            .padding(16.dp)
//            .verticalScroll(rememberScrollState())
//    ) {
//        Text("Add Food", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = NomsyColors.Title)
//        Spacer(Modifier.height(12.dp))
//
//        // Scan button
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text("Scan with a photo:", color = NomsyColors.Texts)
//            Spacer(Modifier.width(8.dp))
//            IconButton(onClick = {
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
//                    == PackageManager.PERMISSION_GRANTED
//                ) {
//                    takePictureLauncher.launch(null)
//                } else {
//                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//                }
//            }) {
//                Icon(Icons.Default.CameraAlt, contentDescription = "Scan", tint = NomsyColors.Title)
//            }
//        }
//        Spacer(Modifier.height(16.dp))
//
//        // Search bar
//        OutlinedTextField(
//            value = query,
//            onValueChange = {
//                query = it
//                // allow manual searches too
//                foodViewModel.searchFood(it)
//            },
//            placeholder = { Text("Search…", color = NomsyColors.Subtitle) },
//            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NomsyColors.Title) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//                .clip(RoundedCornerShape(28.dp)),
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                backgroundColor = NomsyColors.PictureBackground,
//                focusedBorderColor = NomsyColors.Title,
//                unfocusedBorderColor = NomsyColors.Subtitle,
//                cursorColor = NomsyColors.Title,
//                textColor = NomsyColors.Texts
//            ),
//            singleLine = true
//        )
//
//        Spacer(Modifier.height(24.dp))
//
//        // Input fields
//        FormField("Name", name, onValueChange = { name = it }, unit = null)
//        FormField("Calories", calories, onValueChange = { if (it.all { c->c.isDigit() }) calories = it }, unit = "kcal", keyboardType = KeyboardType.Number)
//        FormField("Protein", protein, onValueChange = { if (it.all { c->c.isDigit() }) protein = it }, unit = "g", keyboardType = KeyboardType.Number)
//        FormField("Carbs", carbs, onValueChange = { if (it.all { c->c.isDigit() }) carbs = it }, unit = "g", keyboardType = KeyboardType.Number)
//        FormField("Fats", fats, onValueChange = { if (it.all { c->c.isDigit() }) fats = it }, unit = "g", keyboardType = KeyboardType.Number)
//
//        Spacer(Modifier.height(24.dp))
//
//        // Meal selector
//        Text("Meal", color = NomsyColors.Texts, fontWeight = FontWeight.Medium)
//        Spacer(Modifier.height(8.dp))
//        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            meals.forEach { meal ->
//                Button(
//                    onClick = { selectedMeal = meal },
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = if (meal == selectedMeal) NomsyColors.Title else NomsyColors.PictureBackground,
//                        contentColor = if (meal == selectedMeal) Color.Black else NomsyColors.Texts
//                    ),
//                    shape = RoundedCornerShape(8.dp),
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text(meal)
//                }
//            }
//        }
//
//        Spacer(Modifier.height(32.dp))
//
//        // Placeholder for donut chart
//        Box(
//            Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .background(NomsyColors.PictureBackground, RoundedCornerShape(12.dp)),
//            contentAlignment = Alignment.Center
//        ) {
//            Canvas(modifier = Modifier.size(120.dp)) {
//                // simple static donut
//                drawArc(
//                    color = Color(0xFF1DCD9F),
//                    startAngle = -90f,
//                    sweepAngle = 360f * percentCalories,
//                    useCenter = false,
//                    style = Stroke(width = 20f)
//                )
//                drawArc(
//                    color = Color(0xFF169976),
//                    startAngle = -90f + 360f * percentCalories,
//                    sweepAngle = 360f * (1 - percentCalories),
//                    useCenter = false,
//                    style = Stroke(width = 20f)
//                )
//            }
//            Text("${(percentCalories*100).toInt()} cal", color = NomsyColors.Title, fontWeight = FontWeight.Bold)
//        }
//
//        Spacer(Modifier.height(32.dp))
//
//        // Percent of Daily Goals
//        Text("Percent of Daily Goals", color = NomsyColors.Subtitle, fontWeight = FontWeight.Medium)
//        Spacer(Modifier.height(8.dp))
//
//        GoalRow("Calories", percentCalories, NomsyColors.Title)
//        GoalRow("Carbs",    percentCarbs,    NomsyColors.Texts)
//        GoalRow("Fat",      percentFat,      NomsyColors.Subtitle)
//        GoalRow("Protein",  percentProtein,  NomsyColors.Highlight)
//
//        Spacer(Modifier.height(24.dp))
//
//        Button(
//            onClick = { /* TODO: save food */ },
//            modifier = Modifier.fillMaxWidth().height(50.dp),
//            colors = ButtonDefaults.buttonColors(backgroundColor = NomsyColors.Title, contentColor = Color.Black),
//            shape = RoundedCornerShape(8.dp)
//        ) {
//            Text("Add Food", fontSize = 16.sp, fontWeight = FontWeight.Bold)
//        }
//    }
//}
//
//@Composable
//private fun FormField(
//    label: String,
//    value: String,
//    onValueChange: (String)->Unit,
//    unit: String?,
//    keyboardType: KeyboardType = KeyboardType.Text
//) {
//    Row(
//        Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text("$label:", Modifier.width(80.dp), color = NomsyColors.Texts)
//        OutlinedTextField(
//            value = value,
//            onValueChange = onValueChange,
//            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
//            modifier = Modifier.weight(1f),
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                backgroundColor = NomsyColors.PictureBackground,
//                focusedBorderColor = NomsyColors.Title,
//                unfocusedBorderColor = NomsyColors.Subtitle,
//                textColor = NomsyColors.Texts,
//                cursorColor = NomsyColors.Title
//            ),
//            singleLine = true
//        )
//        unit?.let {
//            Spacer(Modifier.width(8.dp))
//            Text(it, color = NomsyColors.Texts)
//        }
//    }
//}
//
//@Composable
//private fun GoalRow(label: String, fraction: Float, color: Color) {
//    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text(label, color = color)
//            Text("${(fraction*100).toInt()}%", color = color)
//        }
//        LinearProgressIndicator(
//            progress = fraction,
//            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
//            backgroundColor = NomsyColors.PictureBackground,
//            color = color
//        )
//    }
//}
