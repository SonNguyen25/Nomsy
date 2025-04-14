package com.example.nomsy.ui.screens.auth

import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.ui.components.OnboardingBaseScreen
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingBaseScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testBaseScreenContent() {
        var buttonClicked = false

        composeTestRule.setContent {
            OnboardingBaseScreen(
                content = {
                    Text("Test Content")
                },
                buttonText = "Test Button",
                onNextClick = { buttonClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Test Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Button").assertIsDisplayed()

        composeTestRule.onNodeWithText("Test Button").performClick()
        assertTrue(buttonClicked)
    }
}