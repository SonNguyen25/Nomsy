package com.example.nomsy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.nomsy.ui.theme.NomsyColors

@Composable
fun MealTypeSelector(
    selectedMealType: String,
    onMealTypeChange: (String) -> Unit
) {
    val mealOptions = listOf("Breakfast", "Lunch", "Dinner")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        mealOptions.forEach { option ->
            val isSelected = selectedMealType == option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) NomsyColors.Title else NomsyColors.Subtitle,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        color = if (isSelected) NomsyColors.Title else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onMealTypeChange(option) }
                    .padding(vertical = 12.dp)
                    .testTag("MealOption_$option"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) NomsyColors.Background else NomsyColors.Subtitle,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
