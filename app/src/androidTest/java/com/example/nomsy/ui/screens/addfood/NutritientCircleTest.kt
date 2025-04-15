package com.example.nomsy.ui.screens.addfood
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.nomsy.ui.components.NutrientCircle
import com.example.nomsy.ui.theme.NomsyColors
import org.junit.Rule
import org.junit.Test

class NutrientCircleTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun nutrientCircle_displaysLabelAndPercentage() {
        composeTestRule.setContent {
            NutrientCircle(
                label = "Protein",
                percent = 75f,
                color = NomsyColors.Title
            )
        }

        // Check for text label and percentage
        composeTestRule.onNodeWithText("Protein").assertIsDisplayed()
        composeTestRule.onNodeWithText("75%").assertIsDisplayed()

        // Optional: check by testTag
        composeTestRule.onNodeWithTag("NutrientCircle_Protein").assertExists()
    }
}
