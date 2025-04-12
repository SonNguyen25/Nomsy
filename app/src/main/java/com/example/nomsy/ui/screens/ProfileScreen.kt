package com.example.nomsy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomsy.data.local.models.User
import com.example.nomsy.ui.components.ProfileContent
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val username = authViewModel.getCurrentUsername()
    val profileResult by authViewModel.profileResult.observeAsState()

    // Only fetch once when screen first composes (avoids infinite recomposition)
    LaunchedEffect(username) {
        authViewModel.fetchProfileByUsername(username)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        color = NomsyColors.Title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NomsyColors.Background
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: navigate to edit screen */ },
                backgroundColor = NomsyColors.Title,
                contentColor = NomsyColors.Background
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        },
        backgroundColor = NomsyColors.Background,
        content = { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (profileResult) {
                    is Result.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = NomsyColors.Title
                        )
                    }
                    is Result.Error -> {
                        Text(
                            text = "Error loading profile",
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is Result.Success -> {
                        ProfileContent((profileResult as Result.Success<User>).data)
                    }
                    null -> {
                        // initial state before load
                    }
                }
            }
        }
    )
}




