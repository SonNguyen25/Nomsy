package com.example.nomsy.ui.screens.recipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.ui.components.recipesCard
import org.junit.Rule
import org.junit.Test

class RecipeCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleRecipe = Recipe(
        idMeal = "123",
        strMeal = "Spaghetti Bolognese",
        strMealThumb = "https://www.themealdb.com/images/media/meals/sutysw1468247559.jpg",
        strTags = "Pasta,Italian",
        strInstructions = "",
        strYoutube = "",
        strCategory = "",
        strArea = "",
    )


    @Test
    fun recipeCardDisplaysCorrectly() {
        composeTestRule.setContent {
            recipesCard(recipe = sampleRecipe, onClick = {})
        }

        // Compose time and Glide to (try to) load
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("RecipeCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RecipeImage").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RecipeTitle").assertTextContains("Spaghetti Bolognese")
        composeTestRule.onNodeWithTag("RecipeTags").assertTextContains("Pasta • Italian")
    }

    @Test
    fun recipeCardIsClickable() {
        var clicked = false
        composeTestRule.setContent {
            recipesCard(recipe = sampleRecipe, onClick = { clicked = true })
        }

        composeTestRule.onNodeWithTag("RecipeCard").performClick()
        assert(clicked)
    }
}
