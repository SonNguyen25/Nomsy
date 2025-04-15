package com.example.nomsy.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatisticsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    //section titles
    @Test
    fun testStatisticsScreenContent() {
        composeTestRule.setContent {
            StatisticsScreen()
        }
        composeTestRule.onNodeWithText("Statistics")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Daily Calories")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Macronutrients (g)")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Weight Tracking")
            .assertIsDisplayed()
    }


    @Test
    fun testChartContainersDisplayed() {
        composeTestRule.setContent {
            StatisticsScreen()
        }
        composeTestRule.onNodeWithTag("calories-chart")
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("macronutrients-chart")
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("weight-chart")
            .assertExists()
            .assertIsDisplayed()
    }

    /** test charts with espresso
     */
    @Test
    fun testChartsDisplayedWithEspresso() {
        composeTestRule.setContent {
            StatisticsScreen()
        }

        // Check that the charts exist using Espresso
        // This verifies the actual Android View charts are displayed

        // Calories chart
        Espresso.onView(withTagValue(equalTo("barchart_Calories")))
            .check(matches(isDisplayed()))

        // Macronutrients chart
        Espresso.onView(withTagValue(equalTo("multilinechart")))
            .check(matches(isDisplayed()))

        // Weight chart
        Espresso.onView(withTagValue(equalTo("linechart_Weight (kg)")))
            .check(matches(isDisplayed()))
    }

}