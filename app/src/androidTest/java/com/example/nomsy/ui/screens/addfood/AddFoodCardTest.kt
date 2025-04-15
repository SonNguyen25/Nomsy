package com.example.nomsy.ui.screens.addfood

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.data.remote.NutritionTotals
import com.example.nomsy.ui.components.addFoodCard
import org.junit.Rule
import org.junit.Test

class AddFoodCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    @Test
    fun manualSubmission() {
        val fakeViewModel = FakeFoodViewModel().apply {
            dailySummary.postValue(NutritionTotals(2000, 250, 250, 70, 2.0))
        }

        composeTestRule.setContent {
            addFoodCard(
                date = "2024-04-15",
                onDismiss = {},
                onMealAdded = {},
                viewModel = fakeViewModel
            )
        }

        // All input fields share the tag "InputField", so we index them
        composeTestRule.onAllNodesWithTag("InputField")[0].performTextInput("Pasta")
        composeTestRule.onAllNodesWithTag("InputField")[1].performTextInput("500")
        composeTestRule.onAllNodesWithTag("InputField")[2].performTextInput("20")
        composeTestRule.onAllNodesWithTag("InputField")[3].performTextInput("50")
        composeTestRule.onAllNodesWithTag("InputField")[4].performTextInput("10")

        composeTestRule.onNodeWithText("Submit").performClick()

        val request = fakeViewModel.submittedMealRequest
        assert(request != null)
        assert(request?.food_name == "Pasta")
        assert(request?.calories == 500)
        assert(request?.protein == 20)
        assert(request?.carbs == 50)
        assert(request?.fat == 10)
        assert(request?.meal_type == "lunch")
    }

    @Test
    fun pictureSubmission() {
        val fakeViewModel = FakeFoodViewModel().apply {
            dailySummary.postValue(NutritionTotals(2000, 250, 250, 70, 2.0))
        }

        composeTestRule.setContent {
            addFoodCard(
                date = "2024-04-15",
                onDismiss = {},
                onMealAdded = {},
                viewModel = fakeViewModel
            )
        }

        composeTestRule.onNodeWithText("Picture").performClick()

        // Simulate detection
        fakeViewModel.recognizedFood.postValue("Salmon")
        fakeViewModel.foodDetail.postValue(
            Food(
                id = "fake-id-001",
                date = "2024-04-15",
                meal_type = "Lunch",
                food_name = "Salmon",
                calories = 450,
                carbs = 0,
                protein = 30,
                fat = 25
            )
        )

        // Let Compose observe and recompose
        composeTestRule.runOnIdle {}

        // Wait and assert
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("DetectedFood").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("DetectedFood", useUnmergedTree = true)
            .assertTextContains("Salmon", substring = true)


        composeTestRule.onNodeWithText("Submit").performClick()

        val request = fakeViewModel.submittedMealRequest
        assert(request != null)
        assert(request?.food_name == "Salmon")
        assert(request?.calories == 450)
        assert(request?.protein == 30)
        assert(request?.carbs == 0)
        assert(request?.fat == 25)
        assert(request?.meal_type == "lunch")
    }


}