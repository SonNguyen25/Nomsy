package com.example.nomsy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DateSelector(
    date: Long,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val formatter = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
    val dateText = formatter.format(Date(date))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous day"
            )
        }

        Text(
            text = dateText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next day"
            )
        }
    }
}