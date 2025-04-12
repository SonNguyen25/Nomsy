package com.example.nomsy.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.ui.theme.NomsyColors
import recipesCard
import com.example.nomsy.viewmodel.recipeViewModel

@Composable

fun recipesScreen(
    navController: NavController,
    viewModel: recipeViewModel
) {
    val recipes by viewModel.recipes.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Top bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NomsyColors.Background)
                .padding(top = 100.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                "Cook Book",
                style = MaterialTheme.typography.h6,
                color = NomsyColors.Title,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Search bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Food") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(50.dp),
                textStyle = TextStyle(color = Color.Black),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        viewModel.search(searchQuery)
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Recipes list
        Column(modifier = Modifier.fillMaxSize()) {
            recipes.forEach {
                recipesCard(recipe = it)
            }
        }
    }
}

// Fake data for preview/testing
val sampleRecipes = listOf(
    Recipe(
        idMeal = "1",
        strMeal = "Sashimi Combo",
        strInstructions = "Slice and serve chilled.",
        strMealThumb = "https://www.themealdb.com/images/media/meals/1548772327.jpg",
        strYoutube = null,
        strCategory = "Seafood",
        strArea = "Japanese",
        strTags = "Fish,Fresh",
        ingredients = listOf("Egg", "Garlic", "Yellowtail", "Salmon", "Onion")
    ),
    Recipe(
        idMeal = "2",
        strMeal = "Teriyaki Chicken",
        strInstructions = "Grill with teriyaki sauce.",
        strMealThumb = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
        strYoutube = null,
        strCategory = "Chicken",
        strArea = "Japanese",
        strTags = "Grilled,Savory",
        ingredients = listOf("Chicken", "Soy Sauce", "Sugar", "Garlic")
    )
)
