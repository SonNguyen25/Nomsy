package com.example.nomsy.ui.screens.auth

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nomsy.ui.components.OnboardingBaseScreen
import com.example.nomsy.ui.theme.NomsyColors

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
