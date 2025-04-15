package com.example.nomsy.ui.screens.addfood

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.nomsy.ui.components.MealTypeSelector
import org.junit.Rule
import org.junit.Test

class MealTypeSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickUpdateSelectedMealType() {
        var selected = "Breakfast"

        composeTestRule.setContent {
            MealTypeSelector(
                selectedMealType = selected,
                onMealTypeChange = { selected = it }
            )
        }

        // Click "Lunch"
        composeTestRule.onNodeWithTag("MealOption_Lunch").performClick()

        // Recompose with updated state
        composeTestRule.waitForIdle()

        // Click "Dinner"
        composeTestRule.onNodeWithTag("MealOption_Dinner").performClick()

        // Assert that it switched
        assert(selected == "Dinner")
    }

}