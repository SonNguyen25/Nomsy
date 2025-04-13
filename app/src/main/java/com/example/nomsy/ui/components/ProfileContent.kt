package com.example.nomsy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomsy.data.local.models.User
import com.example.nomsy.ui.theme.NomsyColors

@Composable
fun ProfileContent(user: User) {
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // avatar + name + age
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(NomsyColors.PictureBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = NomsyColors.Title,
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = user.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NomsyColors.Title
            )
            Text(
                text = "Age: ${user.age}",
                fontSize = 16.sp,
                color = NomsyColors.Texts
            )
            Spacer(Modifier.height(24.dp))
        }

        item {
            SectionCard(title = "Measurements") {
                KeyValueRow("Height", "${user.height} cm")
                KeyValueRow("Weight", "${user.weight} kg")
            }
            Spacer(Modifier.height(16.dp))
        }

        item {
            SectionCard(title = "Nutrition Goals") {
                KeyValueRow("Water", "${user.nutrition_goals["water"]} L",
//                    color = NomsyColors.Water
                )
                KeyValueRow("Calories", "${user.nutrition_goals["calories"]} kcal")
                KeyValueRow("Protein", "${user.nutrition_goals["protein"]} g")
                KeyValueRow("Carbs", "${user.nutrition_goals["carbs"]} g")
                KeyValueRow("Fat", "${user.nutrition_goals["fat"]} g")
            }
//            Spacer(Modifier.height(32.dp))
        }
    }
}
