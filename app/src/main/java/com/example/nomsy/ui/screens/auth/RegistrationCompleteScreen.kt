package com.example.nomsy.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.AuthViewModel

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
