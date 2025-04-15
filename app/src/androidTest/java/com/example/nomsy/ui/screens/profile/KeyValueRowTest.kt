package com.example.nomsy.ui.screens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.ui.components.KeyValueRow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeyValueRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun labelAndValue() {
        composeTestRule.setContent {
            KeyValueRow(
                label = "Calories",
                value = "2000 kcal"
            )
        }
        composeTestRule.onNodeWithText("Calories")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("2000 kcal")
            .assertIsDisplayed()
    }

    @Test
    fun displaysRows() {
        composeTestRule.setContent {
            KeyValueRow(
                label = "Height",
                value = "175 cm"
            )
            KeyValueRow(
                label = "Weight",
                value = "70 kg"
            )
        }
        composeTestRule.onNodeWithText("Height")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("175 cm")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Weight")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("70 kg")
            .assertIsDisplayed()
    }


}