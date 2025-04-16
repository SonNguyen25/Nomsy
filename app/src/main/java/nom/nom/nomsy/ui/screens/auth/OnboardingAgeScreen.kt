package nom.nom.nomsy.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nom.nom.nomsy.ui.components.OnboardingBaseScreen
import nom.nom.nomsy.ui.theme.NomsyColors
import nom.nom.nomsy.viewModels.IAuthViewModel

@Composable
fun OnboardingAgeScreen(navController: NavController, authViewModel: IAuthViewModel = viewModel()) {
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
                modifier = Modifier.fillMaxWidth(0.8f).testTag("age_input"),
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