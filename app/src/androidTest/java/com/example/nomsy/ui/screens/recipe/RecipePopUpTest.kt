package com.example.nomsy.ui.screens.recipe

import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import com.example.nomsy.data.local.entities.Recipe
import org.hamcrest.Matchers.*
import org.junit.*

class RecipePopUpTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val dummyRecipe = Recipe(
        idMeal = "1",
        strMeal = "Spaghetti Carbonara",
        strMealThumb = "https://example.com/image.jpg",
        strCategory = "Pasta",
        strArea = "Italian",
        strTags = "Dinner",
        strInstructions = "Boil pasta. Fry pancetta. Mix with eggs and cheese.",
        strYoutube = "https://youtube.com/watch?v=dummy",
        ingredients = listOf("Spaghetti", "Eggs", "Pancetta", "Parmesan")
    )

    @Before
    fun setup() {
        Intents.init()
        // Stub out any actual activity start
        intending(any(Intent::class.java)).respondWith(Instrumentation.ActivityResult(0, null))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun clickOnWatchVideoButton() {
        composeTestRule.setContent {
            com.example.nomsy.ui.components.recipePopUp(recipe = dummyRecipe, onDismiss = {})
        }

        composeTestRule.onNodeWithTag("WatchVideoButton").performClick()

        intended(allOf(
            IntentMatchers.hasAction(Intent.ACTION_VIEW),
            IntentMatchers.hasData(Uri.parse("https://youtube.com/watch?v=dummy"))
        ))
    }

    @Test
    fun recipePopUp() {
        var dismissed = false

        composeTestRule.setContent {
            com.example.nomsy.ui.components.recipePopUp(recipe = dummyRecipe, onDismiss = { dismissed = true })
        }

        // Check ui pop up
        composeTestRule.onNodeWithTag("RecipePopUp").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RecipeImage").assertExists()
        composeTestRule.onNodeWithTag("RecipeImage").assertIsDisplayed()

        // Check Information
        composeTestRule.onNodeWithTag("RecipeTitle").assertTextEquals("Spaghetti Carbonara")
        composeTestRule.onNodeWithTag("RecipeCategory").assertTextEquals("Category: Pasta")
        composeTestRule.onNodeWithTag("RecipeArea").assertTextEquals("Area: Italian")
        composeTestRule.onNodeWithTag("Ingredient_0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RecipeInstructions").assertTextContains("Boil pasta. Fry pancetta. Mix with eggs and cheese.")

        // Check if the button is clickable and dismiss the app
        composeTestRule.onNodeWithTag("WatchVideoButton").performClick()
        composeTestRule.onNodeWithTag("CloseButton").performClick()

        assert(dismissed)
    }
}
