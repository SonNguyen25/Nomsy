package com.example.nomsy.ui.components

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.viewModels.FoodViewModel


@Composable
fun PictureCaptureForm() {
    val context = LocalContext.current
    val foodViewModel: FoodViewModel = viewModel()
    val recognizedFood by foodViewModel.recognizedFood.observeAsState("")
    val foodDetail by foodViewModel.foodDetail.observeAsState()

    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cameraPermissionGranted by remember { mutableStateOf(false) }

    // Launcher to take a picture
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        imageBitmap = bitmap
        bitmap?.let {
            foodViewModel.analyzeWithSpoonacular(it)
        }
    }

    // Launcher to request permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted = isGranted
        if (isGranted) {
            takePictureLauncher.launch(null)
        }
        // else do nothing, user denied permission
    }

    val boxSize = 500.dp
    val cornerRadius = 8.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .clip(RoundedCornerShape(cornerRadius))
                .background(NomsyColors.PictureBackground)
                .border(1.dp, NomsyColors.Title, RoundedCornerShape(cornerRadius)),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(cornerRadius)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("Capture to Analyze Calories!",
                    color = NomsyColors.Subtitle,
                    modifier = Modifier.testTag("PlaceholderText"))
            }
        }

        Button(
            onClick = {
                val permission = android.Manifest.permission.CAMERA
                val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                if (isGranted) {
                    takePictureLauncher.launch(null)
                } else {
                    cameraPermissionLauncher.launch(permission)
                }
            },
            modifier = Modifier.testTag("CameraButton"),
            colors = ButtonDefaults.buttonColors(containerColor = NomsyColors.Title)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Take Picture"
            )
            Spacer(Modifier.width(8.dp))
        }

        if (recognizedFood.isNotBlank()) {
            Text(
                text = "Detected: $recognizedFood",
                color = NomsyColors.Title,
                modifier = Modifier.padding(8.dp)
                    .testTag("DetectedFood")
            )
        }

        foodDetail?.let {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.testTag("FoodDetails")) {
                Text("Food: ${it.food_name}", color = NomsyColors.Texts)
                Text("Calories: ${it.calories} kcal", color = NomsyColors.Texts)
                Text("Carbs: ${it.carbs} g", color = NomsyColors.Texts)
                Text("Protein: ${it.protein} g", color = NomsyColors.Texts)
                Text("Fat: ${it.fat} g", color = NomsyColors.Texts)
            }
        }
    }
}
