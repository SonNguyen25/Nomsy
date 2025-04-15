package com.example.nomsy.ui.screens.auth

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.viewModels.AuthViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingFitnessGoalScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        composeTestRule.runOnUiThread {
            val context: Context = ApplicationProvider.getApplicationContext()
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            authViewModel = AuthViewModel(context as Application)

            navController.graph = navController.createGraph(startDestination = "onboarding_fitness_goal") {
                composable("onboarding_fitness_goal") { OnboardingFitnessGoalScreen(navController, authViewModel) }
                composable("onboarding_nutrition") { }
            }
        }

        composeTestRule.setContent {
            OnboardingFitnessGoalScreen(navController, authViewModel)
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithText("What is your fitness goal?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cut").assertIsDisplayed()
        composeTestRule.onNodeWithText("Maintain").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bulk").assertIsDisplayed()
        composeTestRule.onNodeWithText("I'm feeling lucky").assertIsDisplayed()
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun testNoSelectionValidation() {
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_fitness_goal", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testCutSelection() {
        composeTestRule.onNodeWithText("Cut").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_nutrition", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testMaintainSelection() {
        composeTestRule.onNodeWithText("Maintain").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_nutrition", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testBulkSelection() {
        composeTestRule.onNodeWithText("Bulk").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_nutrition", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testRandomSelection() {
        composeTestRule.onNodeWithText("I'm feeling lucky").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_nutrition", navController.currentBackStackEntry?.destination?.route)
    }
}
