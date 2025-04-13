package com.example.nomsy.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.AuthViewModel

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
//                            navController.navigate("add_food") {
//                                popUpTo("login") { inclusive = true }
//                            }
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

