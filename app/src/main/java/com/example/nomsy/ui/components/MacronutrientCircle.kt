package com.example.nomsy.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomsy.ui.theme.NomsyColors

@Composable
fun MacronutrientCircle(
    current: Float,
    goal: Float,
    name: String,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val curPercentage = if (goal > 0) (current / goal).coerceIn(0f, 1f) else 0f
    val animatedPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) curPercentage else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 0
        ),
        label = "Macro Ring Animation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background circle
            drawCircle(
                color = NomsyColors.Subtitle.copy(alpha = 0.3f),
                radius = size.minDimension / 2.5f,
                style = Stroke(width = 12f)
            )

            // Progress arc - draw an arc from top (270 degrees) clockwise
            val sweepAngle = 360f * animatedPercentage.value
            drawArc(
                color = NomsyColors.Title,  // Using Title color for all macros for consistency
                startAngle = 270f, // Start from top
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 12f, cap = StrokeCap.Round),
                size = Size(size.minDimension, size.minDimension),
                topLeft = Offset(
                    (size.width - size.minDimension) / 2f,
                    (size.height - size.minDimension) / 2f
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show actual amount and name
            Text(
                text = "${current.toInt()}g",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = NomsyColors.Texts
            )

            Text(
                text = name,
                fontSize = 12.sp,
                color = NomsyColors.Subtitle,
                textAlign = TextAlign.Center
            )
        }
    }
}