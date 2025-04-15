package com.example.nomsy.ui.screens.auth

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
class OnboardingWeightScreenTest {

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

            navController.graph = navController.createGraph(startDestination = "onboarding_weight") {
                composable("onboarding_weight") { OnboardingWeightScreen(navController, authViewModel) }
                composable("onboarding_fitness_goal") { /* Stub: fitness goal screen */ }
            }
        }

        composeTestRule.setContent {
            OnboardingWeightScreen(navController, authViewModel)
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithText("What's your weight?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("weight_input").assertIsDisplayed()
        composeTestRule.onNodeWithText("kilograms").assertIsDisplayed()
    }

    @Test
    fun testEmptyWeightValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(initialRoute, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testInvalidWeightValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithTag("weight_input").performTextInput("abc")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(initialRoute, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testWeightTooLowValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithTag("weight_input").performTextInput("29")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(initialRoute, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testWeightTooHighValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithTag("weight_input").performTextInput("301")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(initialRoute, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testValidWeightNavigation() {
        composeTestRule.onNodeWithTag("weight_input").performTextInput("70")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_fitness_goal", navController.currentBackStackEntry?.destination?.route)
    }
}
