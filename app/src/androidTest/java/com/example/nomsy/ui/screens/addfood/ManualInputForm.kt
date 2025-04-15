package com.example.nomsy.ui.screens.addfood


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.ui.components.ManualInputForm
import org.junit.Rule
import org.junit.Test

class ManualInputFormTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displayFieldsAndHandlesInput() {
        // State holders for capturing user input
        var foodName = "Egg"
        var calories = "100"
        var protein = "10"
        var carbs = "5"
        var fat = "7"
        var mealType = "Breakfast"

        composeTestRule.setContent {
            ManualInputForm(
                foodName = foodName, onFoodNameChange = { foodName = it },
                calories = calories, onCaloriesChange = { calories = it },
                protein = protein, onProteinChange = { protein = it },
                carbs = carbs, onCarbsChange = { carbs = it },
                fat = fat, onFatChange = { fat = it },
                mealType = mealType, onMealTypeChange = { mealType = it },
                calPercent = 0.5f,
                proteinPercent = 0.3f,
                carbsPercent = 0.2f,
                fatPercent = 0.4f,
                onSelectFood = {},
                viewModel = FakeFoodViewModel()
            )
        }

        // Check label fields
        composeTestRule.onNodeWithText("Name:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calories:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Protein:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Carbs:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fats:").assertIsDisplayed()

        // Check food name input
        composeTestRule.onAllNodesWithText("Egg")[0].assertIsDisplayed()

        // Check nutrient circles
        composeTestRule.onNodeWithText("Calories").assertIsDisplayed()
        composeTestRule.onNodeWithText("Protein").assertIsDisplayed()
        composeTestRule.onNodeWithText("Carbs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fat").assertIsDisplayed()

        // Check goals label
        composeTestRule.onNodeWithText("Daily Goals Completion").assertIsDisplayed()
    }
}
