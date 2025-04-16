package nom.nom.nomsy.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nom.nom.nomsy.data.local.entities.User
import nom.nom.nomsy.data.remote.UpdateProfileRequest
import nom.nom.nomsy.ui.components.FitnessGoalButton
import nom.nom.nomsy.ui.theme.NomsyColors
import nom.nom.nomsy.utils.Result
import nom.nom.nomsy.viewModels.IAuthViewModel
import nom.nom.nomsy.viewModels.IProfileViewModel
import kotlin.random.Random

@Composable
fun EditProfileScreen(
    navController: NavController,
    profileViewModel: IProfileViewModel = viewModel(),
    authViewModel: IAuthViewModel = viewModel()
) {
    val username = authViewModel.getCurrentUsername()
    val profileState by profileViewModel.profile.observeAsState()
    val updateState by profileViewModel.updateResult.observeAsState()

    // form state
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var fitnessGoal by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }

    // load once - fetch profile data when username is available
    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            profileViewModel.fetchByUsername(username)
        }
    }


    LaunchedEffect(profileState) {
        when (profileState) {
            is Result.Success<*> -> {
                val userData = (profileState as Result.Success<User>).data
                name = userData.name ?: ""
                age = userData.age?.toString() ?: ""
                height = userData.height?.toString() ?: ""
                weight = userData.weight?.toString() ?: ""
                fitnessGoal = userData.fitness_goal ?: ""

                // Handle null-safety for nutrition goals
                userData.nutrition_goals?.let { nutritionGoals ->
                    calories = nutritionGoals["calories"]?.toString() ?: ""
                    protein = nutritionGoals["protein"]?.toString() ?: ""
                    carbs = nutritionGoals["carbs"]?.toString() ?: ""
                    fat = nutritionGoals["fat"]?.toString() ?: ""
                    water = nutritionGoals["water"]?.toString() ?: ""
                }
            }
            else -> { /* Do nothing for Loading or Error states */ }
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is Result.Success) {
            profileViewModel.fetchByUsername(username)
            navController.popBackStack()
            profileViewModel.clearUpdateState()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(NomsyColors.Background)
            .padding(24.dp)
    ) {
        Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = NomsyColors.Title
                    )
                }

                Text(
                    text = "Edit Profile",
                    color = NomsyColors.Title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                when (profileState) {
                    is Result.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NomsyColors.Title, modifier = Modifier.testTag("loading"))
                        }
                    }
                    is Result.Error -> {
                        Text(
                            "Error loading profile: ${(profileState as Result.Error).exception.message}",
                            color = MaterialTheme.colors.error
                        )
                    }
                    else -> {}
                }


                if (profileState !is Result.Loading) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name", color = NomsyColors.Texts) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = NomsyColors.Texts,
                            cursorColor = NomsyColors.Texts,
                            focusedBorderColor = NomsyColors.Title,
                            unfocusedBorderColor = NomsyColors.Subtitle,
                            backgroundColor = NomsyColors.PictureBackground
                        )
                    )

                    OutlinedTextField(
                        value = age,
                        onValueChange = { if (it.all(Char::isDigit) || it.isEmpty()) age = it },
                        label = { Text("Age", color = NomsyColors.Texts) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = NomsyColors.Texts,
                            cursorColor = NomsyColors.Texts,
                            focusedBorderColor = NomsyColors.Title,
                            unfocusedBorderColor = NomsyColors.Subtitle,
                            backgroundColor = NomsyColors.PictureBackground
                        )
                    )

                    OutlinedTextField(
                        value = height,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } || it.isEmpty()) height = it },
                        label = { Text("Height (cm)", color = NomsyColors.Texts) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = NomsyColors.Texts,
                            cursorColor = NomsyColors.Texts,
                            focusedBorderColor = NomsyColors.Title,
                            unfocusedBorderColor = NomsyColors.Subtitle,
                            backgroundColor = NomsyColors.PictureBackground
                        )
                    )

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } || it.isEmpty()) weight = it },
                        label = { Text("Weight (kg)", color = NomsyColors.Texts) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = NomsyColors.Texts,
                            cursorColor = NomsyColors.Texts,
                            focusedBorderColor = NomsyColors.Title,
                            unfocusedBorderColor = NomsyColors.Subtitle,
                            backgroundColor = NomsyColors.PictureBackground
                        )
                    )

                    Text(
                        text = "Fitness Goal",
                        color = NomsyColors.Texts,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FitnessGoalButton(
                            text = "Cut",
                            isSelected = fitnessGoal == "cut",
                            onClick = { fitnessGoal = "cut" },
                            modifier = Modifier
                                .testTag("cut")
                                .weight(1f)
                        )

                        FitnessGoalButton(
                            text = "Maintain",
                            isSelected = fitnessGoal == "maintain",
                            onClick = { fitnessGoal = "maintain" },
                            modifier = Modifier
                                .testTag("maintain")
                                .weight(1f)
                        )

                        FitnessGoalButton(
                            text = "Bulk",
                            isSelected = fitnessGoal == "bulk",
                            onClick = { fitnessGoal = "bulk" },
                            modifier = Modifier
                                .testTag("bulk")
                                .weight(1f)
                        )
                    }


                    Text(
                        text = "I'm feeling lucky",
                        color = NomsyColors.Title,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                val goals = listOf("cut", "maintain", "bulk")
                                fitnessGoal = goals[Random.nextInt(goals.size)]
                            }
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                            .testTag("lucky-text")
                    )


                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = water,
                            onValueChange = { if (it.all(Char::isDigit) || it.isEmpty()) water = it },
                            label = { Text("Water (L)", color = NomsyColors.Texts) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = NomsyColors.Texts,
                                cursorColor = NomsyColors.Texts,
                                focusedBorderColor = NomsyColors.Title,
                                unfocusedBorderColor = NomsyColors.Subtitle,
                                backgroundColor = NomsyColors.PictureBackground
                            )
                        )

                        OutlinedTextField(
                            value = calories,
                            onValueChange = { if (it.all(Char::isDigit) || it.isEmpty()) calories = it },
                            label = { Text("Calories", color = NomsyColors.Texts) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = NomsyColors.Texts,
                                cursorColor = NomsyColors.Texts,
                                focusedBorderColor = NomsyColors.Title,
                                unfocusedBorderColor = NomsyColors.Subtitle,
                                backgroundColor = NomsyColors.PictureBackground
                            )
                        )
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = protein,
                            onValueChange = { if (it.all(Char::isDigit) || it.isEmpty()) protein = it },
                            label = { Text("Protein", color = NomsyColors.Texts) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = NomsyColors.Texts,
                                cursorColor = NomsyColors.Texts,
                                focusedBorderColor = NomsyColors.Title,
                                unfocusedBorderColor = NomsyColors.Subtitle,
                                backgroundColor = NomsyColors.PictureBackground
                            )
                        )

                        OutlinedTextField(
                            value = carbs,
                            onValueChange = { if (it.all(Char::isDigit) || it.isEmpty()) carbs = it },
                            label = { Text("Carbs", color = NomsyColors.Texts) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = NomsyColors.Texts,
                                cursorColor = NomsyColors.Texts,
                                focusedBorderColor = NomsyColors.Title,
                                unfocusedBorderColor = NomsyColors.Subtitle,
                                backgroundColor = NomsyColors.PictureBackground
                            )
                        )
                    }

                    OutlinedTextField(
                        value = fat,
                        onValueChange = { if (it.all(Char::isDigit) || it.isEmpty()) fat = it },
                        label = { Text("Fat", color = NomsyColors.Texts) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = NomsyColors.Texts,
                            cursorColor = NomsyColors.Texts,
                            focusedBorderColor = NomsyColors.Title,
                            unfocusedBorderColor = NomsyColors.Subtitle,
                            backgroundColor = NomsyColors.PictureBackground
                        )
                    )

                    Spacer(Modifier.height(24.dp))


                    Button(
                        onClick = {
                            val req = UpdateProfileRequest(
                                name = name,
                                age = age.toIntOrNull(),
                                height = height.toFloatOrNull()?.toInt(),
                                weight = weight.toFloatOrNull()?.toInt(),
                                fitness_goal = fitnessGoal,
                                nutrition_goals = mapOf(
                                    "calories" to calories.toIntOrNull().orZero(),
                                    "protein" to protein.toIntOrNull().orZero(),
                                    "carbs" to carbs.toIntOrNull().orZero(),
                                    "fat" to fat.toIntOrNull().orZero(),
                                    "water" to water.toIntOrNull().orZero()
                                )
                            )
                            profileViewModel.updateProfile(username, req)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = NomsyColors.Title,
                            contentColor = NomsyColors.Background
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Update", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }


                when (updateState) {
                    is Result.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NomsyColors.Title, modifier = Modifier.testTag("loading"))
                        }
                    }
                    is Result.Success -> {
                        Text(
                            "Profile updated!",
                            color = NomsyColors.Title,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    is Result.Error -> Text(
                        "Update failed: ${(updateState as Result.Error).exception.message}",
                        color = MaterialTheme.colors.error
                    )
                    null -> Unit
                }
            }
        }
    }
}

private fun Int?.orZero() = this ?: 0