package com.example.nomsy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomsy.ui.components.BarChartView
import com.example.nomsy.ui.components.LineChartView
import com.example.nomsy.ui.components.MultiLineChartView
import com.example.nomsy.ui.theme.NomsyColors

@Preview(showBackground = true)
@Composable
fun StatisticsScreen() {
    // Fake data for 7 days
    val days     = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
    val calories = listOf(2000f, 2100f, 1900f, 2200f, 2050f, 2000f, 2150f)
    val protein  = listOf(150f, 160f, 155f, 170f, 165f, 160f, 158f)
    val carbs    = listOf(250f, 260f, 240f, 270f, 255f, 250f, 265f)
    val fats     = listOf( 70f,  75f,  68f,  80f,  72f,  70f,  74f)
    val weight   = listOf(70f, 70.2f, 70.1f, 70.3f, 70.2f, 70.4f, 70.3f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NomsyColors.Background)
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Statistics",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = NomsyColors.Title
        )
        Spacer(Modifier.height(24.dp))

        // Calories Bar Chart
        Text(
            text = "Daily Calories",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = NomsyColors.Subtitle,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(8.dp))
        BarChartView(values = calories, labels = days, label = "Calories")

        Spacer(Modifier.height(32.dp))

        // Protein/Carbs/Fat Multi-Line Chart
        Text(
            text = "Macronutrients (g)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = NomsyColors.Subtitle,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(8.dp))
        MultiLineChartView(
            series = listOf(
                "Protein" to protein,
                "Carbs"   to carbs,
                "Fat"     to fats
            ),
            labels = days
        )

        Spacer(Modifier.height(32.dp))

        // Weight Line Chart
        Text(
            text = "Weight Tracking",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = NomsyColors.Subtitle,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(8.dp))
        LineChartView(values = weight, labels = days, label = "Weight (kg)")
    }
}