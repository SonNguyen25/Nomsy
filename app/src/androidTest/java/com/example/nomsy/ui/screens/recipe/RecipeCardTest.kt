package com.example.nomsy.ui.screens.recipe

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.ui.components.recipesCard
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class RecipeCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleRecipe = Recipe(
        idMeal = "1234",
        strMeal = "Broccoli Pasta",
        strMealThumb = "https://www.themealdb.com/images/media/meals/llcbn01574260722.jpg",
        strTags = "Healthy,Quick",
        strInstructions = "Put in hot water",
        strYoutube = "https://youtube.com/ladygaga",
        strCategory = "Fast Food",
        strArea = "China",
        ingredients = emptyList()
    )

    @Before
    fun setUp() {
        composeTestRule.setContent {
            MaterialTheme {
                recipesCard(recipe = sampleRecipe, onClick = {})
            }
        }
    }

    @Test
    fun testMealNameIsDisplayed() {
        composeTestRule.onNodeWithText("Broccoli Pasta").assertExists()
    }

    @Test
    fun testTagsAreDisplayed() {
        composeTestRule.onNodeWithText("Healthy • Quick").assertExists()
    }

    @Test
    fun testCardIsClickable() {
        composeTestRule.onNodeWithText("Broccoli Pasta").assertHasClickAction()
    }
}
