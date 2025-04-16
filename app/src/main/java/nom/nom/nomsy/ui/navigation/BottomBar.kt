package nom.nom.nomsy.ui.navigation

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nom.nom.nomsy.ui.theme.NomsyColors
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {
    val backStack = navController.currentBackStackEntryAsState()
    val currentRoute = backStack.value?.destination?.route

    BottomNavigation(
        backgroundColor = NomsyColors.Background,
        elevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = item.route == currentRoute
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            // popUpTo the start so it doesn't cause a huge backstack
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                // only show label when this item is selected:
                label = { if (selected) Text(item.label) },
                alwaysShowLabel = false,
                selectedContentColor = NomsyColors.Title,
                unselectedContentColor = NomsyColors.Texts
            )
        }
    }
}