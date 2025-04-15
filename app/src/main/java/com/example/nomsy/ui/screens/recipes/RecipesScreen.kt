package com.example.nomsy.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.ui.components.recipesCard
import com.example.nomsy.viewModels.RecipeViewModel
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.example.nomsy.ui.components.recipePopUp
import androidx.compose.ui.platform.testTag
import com.example.nomsy.viewModels.IRecipeViewModel

@Composable


fun recipesScreen(
    navController: NavController,
    viewModel: IRecipeViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllRecipes()

    }

    val recipes by viewModel.recipes.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    val listState = rememberLazyListState()


    Column(modifier = Modifier.fillMaxSize().background(NomsyColors.Background)) {
        // Top bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NomsyColors.Background)
                .padding(top = 100.dp, bottom = 16.dp, start = 10.dp, end = 20.dp)
        ) {
            Text(
                text = "Cook Book",
                fontSize = 28.sp,
                color = NomsyColors.Title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .testTag("CookBookTitle")
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search Food",
                        color = NomsyColors.Texts.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("SearchBar"),
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                textStyle = TextStyle(color = NomsyColors.Texts),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = NomsyColors.Texts,
                    unfocusedBorderColor = NomsyColors.Texts,
                    textColor = NomsyColors.Texts,
                    cursorColor = NomsyColors.Texts,
                    placeholderColor = NomsyColors.Texts.copy(alpha = 0.6f)
                ),
                trailingIcon = {
                    Row {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" },
                                modifier = Modifier.testTag("ClearSearchButton")) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = NomsyColors.Subtitle
                                )
                            }
                        }
                        IconButton(onClick = {
                            focusManager.clearFocus()
                            if (searchQuery.isBlank()) {
                                viewModel.loadAllRecipes()
                            } else {
                                viewModel.search(searchQuery)
                            }
                        }, modifier = Modifier.testTag("SearchIconButton")) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = NomsyColors.Subtitle
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        if (searchQuery.isBlank()) {
                            viewModel.loadAllRecipes()
                        } else {
                            viewModel.search(searchQuery)
                        }
                    }
                )
            )


            Spacer(modifier = Modifier.height(8.dp))
        }

        val recipeMap by viewModel.recipesByCategory.collectAsState()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .testTag("RecipeList"),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            recipeMap.forEach { (category, recipesInCategory) ->
                item {
                    Text(
                        text = category,
                        fontSize = 25.sp,
                        color = NomsyColors.Title,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    val listState = rememberLazyListState()
                    LazyRow(
                        state = listState,
                        flingBehavior = rememberSnapFlingBehavior(listState),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recipesInCategory) { recipe ->
                            recipesCard(
                                recipe = recipe,
                                onClick = { selectedRecipe = recipe },
                                modifier = Modifier.width(240.dp).testTag("RecipeCard_${recipe.idMeal}")
                            )
                        }
                    }
                }
            }
        }

    }

    selectedRecipe?.let { recipe ->
        recipePopUp(recipe = recipe, onDismiss = { selectedRecipe = null })
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
