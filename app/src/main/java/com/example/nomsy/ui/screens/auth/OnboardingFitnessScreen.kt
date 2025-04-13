package com.example.nomsy.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomsy.ui.components.FitnessGoalButton
import com.example.nomsy.ui.components.OnboardingBaseScreen
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.viewModels.AuthViewModel
import kotlin.random.Random

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

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.9f)

            ) {
                FitnessGoalButton(
                    text = "Cut",
                    isSelected = selectedGoal == "cut",
                    onClick = { selectedGoal = "cut" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                FitnessGoalButton(
                    text = "Maintain",
                    isSelected = selectedGoal == "maintain",
                    onClick = { selectedGoal = "maintain" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                FitnessGoalButton(
                    text = "Bulk",
                    isSelected = selectedGoal == "bulk",
                    onClick = { selectedGoal = "bulk" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
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



