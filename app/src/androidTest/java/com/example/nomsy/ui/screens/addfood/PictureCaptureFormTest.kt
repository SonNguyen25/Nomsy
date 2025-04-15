package com.example.nomsy.ui.screens.addfood

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.nomsy.data.local.models.Food
import org.junit.Rule
import org.junit.Test
import com.example.nomsy.ui.components.PictureCaptureForm


class PictureCaptureFormTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showPlaceholderAndCameraButton() {
        val fakeViewModel = FakeFoodViewModel()

        composeTestRule.setContent {
            PictureCaptureForm(viewModel = fakeViewModel)
        }

        composeTestRule.onNodeWithTag("PlaceholderText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CameraButton").assertIsDisplayed()
    }

    @Test
    fun displayFoodAndDetails() {
        val fakeViewModel = FakeFoodViewModel().apply {
            recognizedFood.value = "Broccoli"
            foodDetail.value = Food("1", "2024-04-15", "Lunch", "Broccoli", 55, 10, 4, 1)
        }

        composeTestRule.setContent {
            PictureCaptureForm(viewModel = fakeViewModel)
        }

        composeTestRule.onNodeWithTag("DetectedFood").assertTextContains("Broccoli")
        composeTestRule.onNodeWithTag("FoodDetails").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calories: 55 kcal").assertIsDisplayed()
        composeTestRule.onNodeWithText("Carbs: 10 g").assertIsDisplayed()
        composeTestRule.onNodeWithText("Protein: 4 g").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fat: 1 g").assertIsDisplayed()
    }
}
