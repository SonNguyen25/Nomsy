package com.example.nomsy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nomsy.data.local.models.User
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.AuthViewModel

@Composable
fun AuthScreen(authViewModel: AuthViewModel = viewModel()) {
    var isLogin by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var fitnessGoal by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    // Observe LiveData from the ViewModel
    val loginResult = authViewModel.loginResult?.observeAsState()
    val registerResult = authViewModel.registerResult?.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toggle buttons for login and registration
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { isLogin = true }) { Text("Login") }
            Button(onClick = { isLogin = false }) { Text("Register") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (!isLogin) {
            // Registration additional fields
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = fitnessGoal,
                onValueChange = { fitnessGoal = it },
                label = { Text("Fitness Goal (cut/maintain/bulk)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calories") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text("Protein") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = carbs,
                onValueChange = { carbs = it },
                label = { Text("Carbs") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text("Fat") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (isLogin) {
                    authViewModel.login(username, password)
                } else {
                    val user = User(
                        id = "",
                        username = username,
                        password = password,
                        name = name,
                        age = age.toIntOrNull() ?: 0,
                        height = height.toDoubleOrNull() ?: 0.0,
                        weight = weight.toDoubleOrNull() ?: 0.0,
                        fitness_goal = fitnessGoal,
                        nutrition_goals = mapOf(
                            "calories" to (calories.toIntOrNull() ?: 0),
                            "protein" to (protein.toIntOrNull() ?: 0),
                            "carbs" to (carbs.toIntOrNull() ?: 0),
                            "fat" to (fat.toIntOrNull() ?: 0)
                        )
                    )
                    authViewModel.register(user)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isLogin) "Login" else "Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLogin) {
            loginResult?.value?.let { result ->
                when (result) {
                    is Result.Loading -> Text("Logging in...", modifier = Modifier.align(Alignment.CenterHorizontally))
                    is Result.Success -> Text("Welcome, ${result.data.name}!", modifier = Modifier.align(Alignment.CenterHorizontally))
                    is Result.Error -> Text("Error: ${result.exception.message}", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        } else {
            registerResult?.value?.let { result ->
                when (result) {
                    is Result.Loading -> Text("Registering...", modifier = Modifier.align(Alignment.CenterHorizontally))
                    is Result.Success -> Text("Registered: ${result.data.name}", modifier = Modifier.align(Alignment.CenterHorizontally))
                    is Result.Error -> Text("Error: ${result.exception.message}", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}