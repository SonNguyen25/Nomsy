package com.example.nomsy.ui.screens.auth

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
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingWelcomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.runOnUiThread {
            navController = TestNavHostController(ApplicationProvider.getApplicationContext())
            navController.navigatorProvider.addNavigator(ComposeNavigator())


            navController.graph =
                navController.createGraph(startDestination = "onboarding_welcome") {
                    composable("onboarding_welcome") { OnboardingWelcomeScreen(navController) }
                    composable("onboarding_name") {}
                }
        }
        composeTestRule.setContent {
            OnboardingWelcomeScreen(navController)
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithText("Hello! Let's find out more about you :)")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun testNavigation() {
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_name", navController.currentBackStackEntry?.destination?.route)
    }
}