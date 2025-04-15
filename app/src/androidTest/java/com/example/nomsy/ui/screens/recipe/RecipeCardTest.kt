package com.example.nomsy.ui.screens.recipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.unpackInt1
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

        composeTestRule.onNodeWithTag("RecipeCard", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("RecipeImage", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("RecipeTitle", useUnmergedTree = true)
            .assertTextContains("Spaghetti Bolognese")
        composeTestRule.onNodeWithTag("RecipeTags", useUnmergedTree = true)
            .assertTextContains("Pasta • Italian")
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

    @Test
    fun recipeCardWithNoTagsDisplaysEmptyTags() {
        val noTagRecipe = sampleRecipe.copy(strTags = null)

        composeTestRule.setContent {
            recipesCard(recipe = noTagRecipe, onClick = {})
        }

        composeTestRule.onNodeWithTag("RecipeTags", useUnmergedTree = true).assertTextEquals("")
    }

    @Test
    fun recipeCardWithNoImageDisplayGrayBox() {
        val noImageRecipe = sampleRecipe.copy(strMealThumb = "")

        composeTestRule.setContent {
            recipesCard(recipe = noImageRecipe, onClick = {})
        }

        composeTestRule.onNodeWithTag("RecipeImage", useUnmergedTree = true)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun recipeCardWithLongTitle_doesNotCrash() {
        val longTitle = "This is a very very very long meal name that should be ellipsized"
        val longTitleRecipe = sampleRecipe.copy(strMeal = longTitle)

        composeTestRule.setContent {
            recipesCard(recipe = longTitleRecipe, onClick = {})
        }

        composeTestRule.onNodeWithTag("RecipeTitle", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(longTitle)
    }


    @Test
    fun recipeCardWithNoTags() {
        val noTagRecipe = sampleRecipe.copy(strTags = null)

        composeTestRule.setContent {
            recipesCard(recipe = noTagRecipe, onClick = {})
        }

        composeTestRule.onNodeWithTag("RecipeTags", useUnmergedTree = true).assertTextEquals("")
    }

}