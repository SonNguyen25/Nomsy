package com.example.nomsy.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.ui.components.FitnessGoalButton
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FitnessGoalButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testButtonStates() {
        var clicked = false

        composeTestRule.setContent {
            Column {
                FitnessGoalButton(
                    text = "Test Button",
                    isSelected = false,
                    onClick = { clicked = true }
                )

                FitnessGoalButton(
                    text = "Selected Button",
                    isSelected = true,
                    onClick = { }
                )
            }
        }
        composeTestRule.onNodeWithText("Test Button")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(clicked)
        composeTestRule.onNodeWithText("Selected Button")
            .assertIsDisplayed()
            .assertHasClickAction()
    }


    @Test
    fun testMultipleButtonSelect() {
        var selectedGoal = "Lose Weight"

        composeTestRule.setContent {
            Column {
                listOf("Lose Weight", "Maintain", "Gain Muscle").forEach { goal ->
                    FitnessGoalButton(
                        text = goal,
                        isSelected = goal == selectedGoal,
                        onClick = { selectedGoal = goal }
                    )
                }
            }
        }
        composeTestRule.onNodeWithText("Lose Weight")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Gain Muscle")
            .performClick()
        assertEquals("Gain Muscle", selectedGoal)
        composeTestRule.onNodeWithText("Maintain")
            .performClick()
        assertEquals("Maintain", selectedGoal)
    }

}