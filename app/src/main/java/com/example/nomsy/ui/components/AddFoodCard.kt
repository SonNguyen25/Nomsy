package com.example.nomsy.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.ui.theme.NomsyColors

@Composable
fun addFoodCard(
    food: Food? = null,
    onDismiss: () -> Unit
) {
    var inputMethod by remember { mutableStateOf("Manual") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Manual", "Picture").forEach { method ->
                    Text(
                        text = method,
                        color = if (inputMethod == method) NomsyColors.Title else NomsyColors.Subtitle,
                        modifier = Modifier
                            .clickable { inputMethod = method }
                            .padding(8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (inputMethod) {
                "Manual" -> ManualInputForm()
                "Picture" -> PictureCaptureSection()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    }
}

@Composable
fun ManualInputForm() {
    // Placeholder form
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Food Name", "Calories", "Carbs", "Protein", "Fat").forEach { label ->
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PictureCaptureSection() {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        // handle image recognition here
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { launcher.launch(null) }) {
            Text("Take a Picture")
        }
    }
}
