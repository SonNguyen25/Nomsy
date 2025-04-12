package com.example.nomsy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Statistics : BottomNavItem("statistics", "Statistics", Icons.Default.ShowChart)
    object Home       : BottomNavItem("home",       "Home",       Icons.Default.Home)
    object Recipes    : BottomNavItem("recipes",    "Recipes",    Icons.Default.RestaurantMenu)
    object Profile    : BottomNavItem("profile",    "Profile",    Icons.Default.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Statistics,
    BottomNavItem.Home,
    BottomNavItem.Recipes,
    BottomNavItem.Profile
)
