package com.example.nomsy.ui.screens.recipe

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.example.nomsy.ui.screens.recipes.recipesScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())


    @Test
    fun displaysTitleAndRecipeCardAndOpensPopup() {
        val fakeViewModel = FakeRecipeViewModel()

        composeTestRule.setContent {
            recipesScreen(navController = navController, viewModel = fakeViewModel)
        }

        // Check title is displayed
        composeTestRule.onNodeWithTag("CookBookTitle").assertIsDisplayed()

        // Check RecipeList loads (Wait for lazy column items)
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("RecipeList").fetchSemanticsNodes().isNotEmpty()
        }

        // Wait for card with tag
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("RecipeCard_1").fetchSemanticsNodes().isNotEmpty()
        }

        // Click the card
        composeTestRule.onNodeWithTag("RecipeCard_1").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("RecipeTitle", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithTag("RecipePopUp", useUnmergedTree = true)
            .onChildren()
            .filter(hasTestTag("RecipeTitle"))
            .assertCountEquals(1)
    }

    @Test
    fun searchFiltersResults() {
        val fakeViewModel = FakeRecipeViewModel()

        composeTestRule.setContent {
            recipesScreen(navController = navController, viewModel = fakeViewModel)
        }

        // Enter search text
        composeTestRule.onNodeWithTag("SearchBar").performTextInput("Pasta")

        // Tap the search icon
        composeTestRule.onNodeWithTag("SearchIconButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithTag("RecipeCard_3").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("RecipeCard_3").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RecipeCard_1").assertDoesNotExist()
    }

    @Test
    fun displaysCategoryAndRecipeCard() {
        val fakeViewModel = FakeRecipeViewModel()
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext<Context>())

        composeTestRule.setContent {
            recipesScreen(
                navController = navController,
                viewModel = fakeViewModel
            )
        }

        composeTestRule.onNodeWithTag("CookBookTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SearchBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RecipeCard_1").assertIsDisplayed()
    }
}
