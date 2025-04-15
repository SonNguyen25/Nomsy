package com.example.nomsy.ui.screens.addfood

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.ui.components.SearchFoodDropdown
import com.example.nomsy.viewModels.FoodViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchFoodDropdownTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchFoodDropdown_displaysResults_and_selectsItem() {
        val fakeFoods = listOf(
            Food("1", "2024-04-15", "Lunch", "Apple", 95, 25, 0, 0),
            Food("2", "2024-04-15", "Lunch", "Banana", 105, 27, 1, 0)
        )

        var selectedFood: Food? = null

        composeTestRule.setContent {
            val fakeViewModel = FakeFoodViewModel().apply {
                searchResults.addAll(fakeFoods)
            }

            SearchFoodDropdown(
                viewModel = fakeViewModel,
                foodName = "",
                onSelectFood = { selectedFood = it },
                onQueryChange = {}
            )
        }

        //check if placeholder appears
        composeTestRule.onNodeWithText("Search Food").assertIsDisplayed()

        // Simulate user typing
        composeTestRule.onNodeWithTag("SearchTextField").performTextInput("App")

        // Wait for debounce coroutine
        composeTestRule.waitForIdle()

        // Dropdown appears
        composeTestRule.onNodeWithTag("SearchDropdown").assertIsDisplayed()

        // Click Apple
        composeTestRule.onNodeWithTag("SearchResult_Apple").performClick()

        assertEquals("Apple", selectedFood?.food_name)
    }

    @Test
    fun searchFoodDropdown_clearButtonResetsQueryAndDropdown() {
        composeTestRule.setContent {
            val fakeViewModel = FakeFoodViewModel().apply {
                searchResults.add(Food("1", "2024-04-15", "Lunch", "Apple", 95, 25, 0, 0))
            }

            SearchFoodDropdown(
                viewModel = fakeViewModel,
                foodName = "",
                onSelectFood = {},
                onQueryChange = {}
            )
        }

        // Type something
        composeTestRule.onNodeWithTag("SearchTextField").performTextInput("App")
        composeTestRule.waitForIdle()

        // Check dropdown is expanded
        composeTestRule.onNodeWithTag("SearchDropdown").assertIsDisplayed()

        // Click the clear button
        composeTestRule.onNodeWithContentDescription("Clear").performClick()

        // Check the text is reset and dropdown is not visible
        composeTestRule.onNodeWithTag("SearchTextField", useUnmergedTree = true).assertTextEquals("")
        composeTestRule.onNodeWithTag("SearchDropdown").assertDoesNotExist()
    }


    @Test
    fun searchFoodDropdown_searchIconClearsFocus() {
        val fakeFoods = listOf(
            Food("1", "2024-04-15", "Lunch", "Banana", 105, 27, 1, 0)
        )

        val fakeViewModel = FakeFoodViewModel().apply {
            searchResults.addAll(fakeFoods)
        }

        composeTestRule.setContent {
            SearchFoodDropdown(
                viewModel = fakeViewModel,
                foodName = "",
                onSelectFood = {},
                onQueryChange = {}
            )
        }

        // Type something to activate dropdown
        composeTestRule.onNodeWithTag("SearchTextField").performTextInput("Banana")
        composeTestRule.waitForIdle()

        // Click the search icon to trigger focus clear
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.waitForIdle()

        // Now the dropdown should still be visible if expanded = true
        composeTestRule.onNodeWithTag("SearchDropdown").assertIsDisplayed()
    }



}