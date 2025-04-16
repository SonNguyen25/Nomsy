package nom.nom.nomsy.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
fun OnboardingWeightScreen(
    navController: NavController,
    authViewModel: IAuthViewModel = viewModel()
) {
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
                    modifier = Modifier
                        .weight(0.7f)
                        .testTag("weight_input")
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
                    Toast.makeText(context, "Please enter a valid weight", Toast.LENGTH_SHORT)
                        .show()
                }

                weight.toDouble() < 30 || weight.toDouble() > 300 -> {
                    Toast.makeText(
                        context,
                        "Please enter a realistic weight (30-300 kg)",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    authViewModel.setUserWeight(weight.toInt())
                    navController.navigate("onboarding_fitness_goal")
                }
            }
        }
    )
}