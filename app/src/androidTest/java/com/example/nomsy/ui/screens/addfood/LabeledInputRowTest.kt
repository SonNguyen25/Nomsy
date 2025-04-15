package com.example.nomsy.ui.screens.addfood

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import androidx.compose.runtime.*
import com.example.nomsy.ui.components.LabeledInputRow

class LabeledInputRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun labeledWithUnit() {
        composeTestRule.setContent {
            var text by remember { mutableStateOf("123") }
            LabeledInputRow(
                label = "Calories",
                value = text,
                onValueChange = { text = it },
                unit = "kcal",
                isNumeric = true
            )
        }

        //Check that all fields have the same fields
        composeTestRule.onNodeWithTag("LabeledInputRow").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InputLabel").assertTextContains("Calories")
        composeTestRule.onNodeWithTag("InputField").assertTextContains("123")
        composeTestRule.onNodeWithTag("InputUnit").assertTextContains("kcal")
    }

    @Test
    fun labeledInputRowAcceptsOnlyNumbers_whenIsNumericTrue() {
        composeTestRule.setContent {
            var text by remember { mutableStateOf("") }
            LabeledInputRow(
                label = "Calories",
                value = text,
                onValueChange = { text = it },
                unit = "kcal",
                isNumeric = true
            )
        }

        val inputField = composeTestRule.onNodeWithTag("InputField")

        // Try to input a string (should not be accepted)
        inputField.performTextInput("abc123")
        inputField.assertTextEquals("")

        // Input numbers only
        inputField.performTextInput("456")
        inputField.assertTextEquals("456")
    }

    @Test
    fun labeledInputRowAcceptsAnyText_whenIsNumericFalse() {
        composeTestRule.setContent {
            var text by remember { mutableStateOf("") }
            LabeledInputRow(
                label = "Comment",
                value = text,
                onValueChange = { text = it },
                unit = null,
                isNumeric = false
            )
        }

        val inputField = composeTestRule.onNodeWithTag("InputField")

        inputField.performTextInput("abc123")
        inputField.assertTextEquals("abc123")
    }



}
