package com.example.nomsy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.ui.theme.NomsyColors

@Composable
fun MealListSection(
    title: String,
    meals: List<MealItem>,
    onDelete: (MealItem) -> Unit = {}
) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = NomsyColors.Title,
    )

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = NomsyColors.Subtitle.copy(alpha = 0.5f)
    )

    if (meals.isEmpty()) {
        Text(
            text = "No foods added yet",
            color = NomsyColors.Subtitle,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    } else {
        Column {
            meals.forEach { meal ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = meal.food_name,
                        color = NomsyColors.Texts,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${meal.calories} kcal",
                            color = NomsyColors.Title
                        )

                        IconButton(
                            onClick = { onDelete(meal) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = NomsyColors.Subtitle
                            )
                        }
                    }
                }
            }
        }
    }
}