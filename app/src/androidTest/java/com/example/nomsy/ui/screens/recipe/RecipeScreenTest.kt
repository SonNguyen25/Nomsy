package com.example.nomsy.ui.screens.recipe

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.nomsy.viewModels.RecipeViewModel
import com.example.nomsy.ui.screens.recipes.recipesScreen
import com.example.nomsy.data.repository.IRecipeRepository
import com.example.nomsy.data.local.models.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.nomsy.ui.screens.recipe.FakeRecipeRepository

class RecipeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: RecipeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)
        viewModel = RecipeViewModel(FakeRecipeRepository())
        viewModel.loadAllRecipes()
    }

    @Test
    fun displaysTitleAndRecipeCardAndOpensPopup() = runTest(testDispatcher) {
        // Load data first to avoid LaunchedEffect racing
        viewModel.loadAllRecipes()

        composeTestRule.setContent {
            recipesScreen(
                navController = rememberNavController(),
                viewModel = viewModel
            )
        }

        // Let Compose render updates after stateFlow emits
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            viewModel.recipes.value.isNotEmpty()
        }

        // Force Compose to settle before asserting
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("CookBookTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Category_Seafood").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RecipeCard_1").assertIsDisplayed()
    }


}
