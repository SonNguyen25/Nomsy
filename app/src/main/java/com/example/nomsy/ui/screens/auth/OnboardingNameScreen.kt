package com.example.nomsy.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomsy.ui.components.OnboardingBaseScreen
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.viewModels.AuthViewModel

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
                modifier = Modifier.testTag("name_input").fillMaxWidth(0.8f)
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







