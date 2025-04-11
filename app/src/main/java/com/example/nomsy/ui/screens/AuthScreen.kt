package com.example.nomsy.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomsy.data.local.models.User
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.AuthViewModel
import kotlin.random.Random

// Define Nomsy colors
object NomsyColors {
    val Background = Color(0xFF000000)
    val Title = Color(0xFF1DCD9F)
    val Subtitle = Color(0xFF169976)
    val Highlight = Color(0xFF000000)
    val Texts = Color(0xFF1DCD9F)
    val Water = Color(0xFF123458)
    val PictureBackground = Color(0xFF222222)
}

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginResult = authViewModel.loginResult?.observeAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NomsyColors.Background)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "NOMSY",
                color = NomsyColors.Title,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(80.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = NomsyColors.Texts) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Username",
                        tint = NomsyColors.Title
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = NomsyColors.Texts) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = NomsyColors.Title
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = NomsyColors.Title,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    when {
                        username.isEmpty() -> {
                            Toast.makeText(context, "Please enter a username", Toast.LENGTH_SHORT).show()
                        }
                        password.isEmpty() -> {
                            Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            authViewModel.login(username, password)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = NomsyColors.Title,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Sign In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("Don't have an account? ")
                    withStyle(style = SpanStyle(
                        color = NomsyColors.Title,
                        textDecoration = TextDecoration.Underline
                    )) {
                        append("Sign up!")
                    }
                },
                color = NomsyColors.Texts,
                modifier = Modifier.clickable { navController.navigate("register") }
            )

            // Show login status
            loginResult?.value?.let { result ->
                Spacer(modifier = Modifier.height(16.dp))
                when (result) {
                    is Result.Loading -> CircularProgressIndicator(color = NomsyColors.Title)
                    is Result.Success -> {
                        Text(
                            "Welcome, ${result.data.name}!",
                            color = NomsyColors.Title
                        )
                        // Navigate to home screen after successful login
                        LaunchedEffect(key1 = true) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    is Result.Error -> Text(
                        "Error: ${result.exception.message}",
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NomsyColors.Background)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "NOMSY",
                color = NomsyColors.Title,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(60.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = NomsyColors.Texts) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = NomsyColors.Title
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = NomsyColors.Texts) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Username",
                        tint = NomsyColors.Title
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = NomsyColors.Texts) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = NomsyColors.Title
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = NomsyColors.Title,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", color = NomsyColors.Texts) },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirm Password",
                        tint = NomsyColors.Title
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                        tint = NomsyColors.Title,
                        modifier = Modifier.clickable { confirmPasswordVisible = !confirmPasswordVisible }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // Validate inputs before proceeding
                    when {
                        email.isEmpty() -> {
                            Toast.makeText(context, "Please enter an email", Toast.LENGTH_SHORT).show()
                        }
                        !(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) -> {
                            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        }
                        username.isEmpty() -> {
                            Toast.makeText(context, "Please enter a username", Toast.LENGTH_SHORT).show()
                        }
                        password.isEmpty() -> {
                            Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
                        }
                        password.length < 6 -> {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        }
                        password != confirmPassword -> {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // Store credentials in view model and proceed to onboarding
                            authViewModel.setCredentials(username, password, email)
                            navController.navigate("onboarding_welcome")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = NomsyColors.Title,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(style = SpanStyle(
                        color = NomsyColors.Title,
                        textDecoration = TextDecoration.Underline
                    )) {
                        append("Sign in!")
                    }
                },
                color = NomsyColors.Texts,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}

// Onboarding screens
@Composable
fun OnboardingWelcomeScreen(navController: NavController) {
    OnboardingBaseScreen(
        content = {
            Text(
                text = "Hello! Let's find out more about you :)",
                color = NomsyColors.Title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        },
        buttonText = "Next",
        onNextClick = { navController.navigate("onboarding_name") }
    )
}

@Composable
fun OnboardingNameScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current

    OnboardingBaseScreen(
        content = {
            Text(
                text = "What's your name?",
                color = NomsyColors.Title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { input ->
                    // Only allow alphabetic characters and spaces
                    if (input.all { it.isLetter() || it.isWhitespace() }) {
                        name = input
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    focusedBorderColor = NomsyColors.Title,
                    unfocusedBorderColor = NomsyColors.Subtitle,
                    backgroundColor = NomsyColors.PictureBackground
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        },
        buttonText = "Next",
        onNextClick = {
            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                authViewModel.setUserName(name)
                navController.navigate("onboarding_age")
            }
        }
    )
}

@Composable
fun OnboardingAgeScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var age by remember { mutableStateOf("") }
    val context = LocalContext.current

    OnboardingBaseScreen(
        content = {
            Text(
                text = "What's your age?",
                color = NomsyColors.Title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { input ->
                    // Only allow numeric values
                    if (input.all { it.isDigit() }) {
                        age = input
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
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        },
        buttonText = "Next",
        onNextClick = {
            when {
                age.isEmpty() -> {
                    Toast.makeText(context, "Please enter your age", Toast.LENGTH_SHORT).show()
                }
                age.toIntOrNull() == null -> {
                    Toast.makeText(context, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                }
                age.toInt() < 13 -> {
                    Toast.makeText(context, "You must be at least 13 years old", Toast.LENGTH_SHORT).show()
                }
                age.toInt() > 120 -> {
                    Toast.makeText(context, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    authViewModel.setUserAge(age.toInt())
                    navController.navigate("onboarding_height")
                }
            }
        }
    )
}

@Composable
fun OnboardingHeightScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var height by remember { mutableStateOf("") }
    val context = LocalContext.current

    OnboardingBaseScreen(
        content = {
            Text(
                text = "What's your height?",
                color = NomsyColors.Title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                OutlinedTextField(
                    value = height,
                    onValueChange = { input ->
                        // Allow numbers and decimal point
                        if (input.all { it.isDigit() || it == '.' }) {
                            height = input
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
                    modifier = Modifier.weight(0.7f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "centimeters",
                    color = NomsyColors.Texts,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(0.3f)
                )
            }
        },
        buttonText = "Next",
        onNextClick = {
            when {
                height.isEmpty() -> {
                    Toast.makeText(context, "Please enter your height", Toast.LENGTH_SHORT).show()
                }
                height.toDoubleOrNull() == null -> {
                    Toast.makeText(context, "Please enter a valid height", Toast.LENGTH_SHORT).show()
                }
                height.toDouble() < 50 || height.toDouble() > 250 -> {
                    Toast.makeText(context, "Please enter a realistic height (50-250 cm)", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    authViewModel.setUserHeight(height.toDouble())
                    navController.navigate("onboarding_weight")
                }
            }
        }
    )
}

@Composable
fun OnboardingWeightScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var weight by remember { mutableStateOf("") }
    val context = LocalContext.current

    OnboardingBaseScreen(
        content = {
            Text(
                text = "What's your weight?",
                color = NomsyColors.Title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { input ->
                        // Allow numbers and decimal point
                        if (input.all { it.isDigit() || it == '.' }) {
                            weight = input
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
                    modifier = Modifier.weight(0.7f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "kilograms",
                    color = NomsyColors.Texts,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(0.3f)
                )
            }
        },
        buttonText = "Next",
        onNextClick = {
            when {
                weight.isEmpty() -> {
                    Toast.makeText(context, "Please enter your weight", Toast.LENGTH_SHORT).show()
                }
                weight.toDoubleOrNull() == null -> {
                    Toast.makeText(context, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
                }
                weight.toDouble() < 30 || weight.toDouble() > 300 -> {
                    Toast.makeText(context, "Please enter a realistic weight (30-300 kg)", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    authViewModel.setUserWeight(weight.toDouble())
                    navController.navigate("onboarding_fitness_goal")
                }
            }
        }
    )
}

@Composable
fun OnboardingFitnessGoalScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var selectedGoal by remember { mutableStateOf("") }
    val context = LocalContext.current

    OnboardingBaseScreen(
        content = {
            Text(
                text = "What is your fitness goal?",
                color = NomsyColors.Title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                FitnessGoalButton(
                    text = "Cut",
                    isSelected = selectedGoal == "cut",
                    onClick = { selectedGoal = "cut" },
                    modifier = Modifier.weight(1f)
                )

                FitnessGoalButton(
                    text = "Maintain",
                    isSelected = selectedGoal == "maintain",
                    onClick = { selectedGoal = "maintain" },
                    modifier = Modifier.weight(1f)
                )

                FitnessGoalButton(
                    text = "Bulk",
                    isSelected = selectedGoal == "bulk",
                    onClick = { selectedGoal = "bulk" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "I'm feeling lucky",
                color = NomsyColors.Title,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    val goals = listOf("cut", "maintain", "bulk")
                    selectedGoal = goals[Random.nextInt(goals.size)]
                }
            )
        },
        buttonText = "Next",
        onNextClick = {
            if (selectedGoal.isEmpty()) {
                Toast.makeText(context, "Please select a fitness goal", Toast.LENGTH_SHORT).show()
            } else {
                authViewModel.setUserFitnessGoal(selectedGoal)
                navController.navigate("onboarding_nutrition")
            }
        }
    )
}

@Composable
fun OnboardingNutritionScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

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
                    modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth()
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
                        } else -> {
                            // Default values if data is missing
                            2000
                        }
                    }

                    // Set calculated values
                    calories = basalMetabolicRate.toString()
                    protein = (userWeight * 1.8).toInt().toString() // 1.8g protein per kg of body weight
                    carbs = (basalMetabolicRate * 0.4 / 4).toInt().toString() // 40% of calories from carbs
                    fat = (basalMetabolicRate * 0.3 / 9).toInt().toString() // 30% of calories from fat
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
                    Toast.makeText(context, "Please enter a valid number for calories", Toast.LENGTH_SHORT).show()
                }
                protein.isEmpty() -> {
                    Toast.makeText(context, "Please enter protein", Toast.LENGTH_SHORT).show()
                }
                protein.toIntOrNull() == null -> {
                    Toast.makeText(context, "Please enter a valid number for protein", Toast.LENGTH_SHORT).show()
                }
                carbs.isEmpty() -> {
                    Toast.makeText(context, "Please enter carbs", Toast.LENGTH_SHORT).show()
                }
                carbs.toIntOrNull() == null -> {
                    Toast.makeText(context, "Please enter a valid number for carbs", Toast.LENGTH_SHORT).show()
                }
                fat.isEmpty() -> {
                    Toast.makeText(context, "Please enter fat", Toast.LENGTH_SHORT).show()
                }
                fat.toIntOrNull() == null -> {
                    Toast.makeText(context, "Please enter a valid number for fat", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // All inputs are non-empty and valid integers
                    val nutritionGoals = mapOf(
                        "calories" to calories.toInt(),
                        "protein"  to protein.toInt(),
                        "carbs"    to carbs.toInt(),
                        "fat"      to fat.toInt()
                    )

                    authViewModel.setUserNutritionGoals(nutritionGoals)

                    val user = User(
                        id               = "",
                        username         = authViewModel.getUsername(),
                        password         = authViewModel.getPassword(),
                        name             = authViewModel.getUserName(),
                        age              = authViewModel.getUserAge(),
                        height           = authViewModel.getUserHeight(),
                        weight           = authViewModel.getUserWeight(),
                        fitness_goal     = authViewModel.getUserFitnessGoal(),
                        nutrition_goals  = nutritionGoals
                    )

                    authViewModel.register(user)
                    navController.navigate("registration_complete")
                }
            }
        }
    )
}


@Composable
fun RegistrationCompleteScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val registerResult = authViewModel.registerResult?.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NomsyColors.Background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            registerResult?.value?.let { result ->
                when (result) {
                    is Result.Loading -> {
                        CircularProgressIndicator(color = NomsyColors.Title)
                        Text(
                            text = "Creating your account...",
                            color = NomsyColors.Texts,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    is Result.Success -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Success",
                            tint = NomsyColors.Title,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Welcome to Nomsy, ${result.data.name}!",
                            color = NomsyColors.Title,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your account has been created successfully.",
                            color = NomsyColors.Texts,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = NomsyColors.Title,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Get Started")
                        }
                    }
                    is Result.Error -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Registration Error",
                            color = Color.Red,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error: ${result.exception.message}",
                            color = NomsyColors.Texts,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = NomsyColors.Title,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }
    }
}

// Helper composables
@Composable
fun OnboardingBaseScreen(
    content: @Composable () -> Unit,
    buttonText: String,
    onNextClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NomsyColors.Background)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            content()

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = NomsyColors.Title,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FitnessGoalButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) NomsyColors.Title else NomsyColors.PictureBackground,
            contentColor = if (isSelected) Color.Black else NomsyColors.Texts
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.height(48.dp)
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

