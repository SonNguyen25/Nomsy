package com.example.nomsy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nomsy.ui.theme.NomsyColors
import kotlin.math.pow
import kotlin.math.round

@Composable
fun WaterIntakeBar(
    currentIntake: Float,
    goal: Float,
    onWaterIntakeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var waterAmount by remember { mutableStateOf(currentIntake) }

    LaunchedEffect(currentIntake) {
        waterAmount = currentIntake
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalDrink,
                contentDescription = null,
                tint = NomsyColors.Water
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Water Intake",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NomsyColors.Title
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${waterAmount.format(1)} / ${goal.format(1)} L",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = NomsyColors.Texts
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Water intake bar - click to open dialog
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .border(
                    width = 2.dp,
                    color = NomsyColors.Water,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { showDialog = true }
        ) {
            // Filled portion
            Box(
                modifier = Modifier
                    .fillMaxWidth((waterAmount / goal).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .padding(2.dp)
                    .background(
                        color = NomsyColors.Water.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(18.dp)
                    )
            )

            // Text
            Text(
                text = "${waterAmount.format(1)} L",
                modifier = Modifier.align(Alignment.Center),
                color = NomsyColors.Texts,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // Water intake dialog
    if (showDialog) {
        WaterIntakeDialog(
            currentIntake = waterAmount,
            onDismiss = { showDialog = false },
            onConfirm = {
                onWaterIntakeChange(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun WaterIntakeDialog(
    currentIntake: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var waterAmount by remember { mutableStateOf(currentIntake) }
    var textInput by remember { mutableStateOf(currentIntake.format(1)) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = NomsyColors.PictureBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Update Water Intake",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = NomsyColors.Title
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Water amount display with buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (waterAmount > 0) {
                                waterAmount = (waterAmount - 0.1f).coerceAtLeast(0f)
                            }

                            textInput = waterAmount.format(1)
                        }
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = NomsyColors.Title
                        )
                    }

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = {
                            textInput = it
                            waterAmount = it.toFloatOrNull() ?: waterAmount
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        label = { Text("Water (L)", color = NomsyColors.Subtitle) },
                        modifier = Modifier.width(150.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = NomsyColors.Texts,
                            cursorColor = NomsyColors.Title,
                            focusedBorderColor = NomsyColors.Title,
                            unfocusedBorderColor = NomsyColors.Subtitle
                        )
                    )

                    IconButton(
                        onClick = {
                            if (waterAmount < 2.05) {
                                waterAmount = (waterAmount + 0.1f).coerceAtMost(2f)
                            }
                            textInput = waterAmount.format(1)
                        }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = NomsyColors.Title
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            "Cancel",
                            color = NomsyColors.Subtitle
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirm(waterAmount) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NomsyColors.Title,
                            contentColor = NomsyColors.Background
                        )
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}

// Extension function to format float to 1 decimal place as string
fun Float.format(digits: Int): String {
    val multiplier = 10.0.pow(digits.toDouble()).toFloat()
    return (round(this * multiplier) / multiplier).toString()
}

