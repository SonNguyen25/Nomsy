package com.example.nomsy.ui.screens.auth

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
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
class OnboardingHeightScreenTest {

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
            navController.graph = navController.createGraph(startDestination = "onboarding_height") {
                composable("onboarding_height") { OnboardingHeightScreen(navController, authViewModel) }
                composable("onboarding_weight") { }
            }
        }
        composeTestRule.setContent {
            OnboardingHeightScreen(navController, authViewModel)
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithText("What's your height?").assertIsDisplayed()
        composeTestRule.onAllNodes(hasSetTextAction()).onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("centimeters").assertIsDisplayed()
    }

    @Test
    fun testEmptyHeightValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(initialRoute, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testInvalidHeightValidation() {
        val heightField = composeTestRule.onAllNodes(hasSetTextAction()).onFirst()
        heightField.performTextInput("abc")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_height", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testHeightOutOfRangeValidation() {
        val heightField = composeTestRule.onAllNodes(hasSetTextAction()).onFirst()

        heightField.performTextInput("49")
        composeTestRule.onNodeWithText("Next").performClick()
        var currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals("onboarding_height", currentRoute)

        heightField.performTextInput("251")
        composeTestRule.onNodeWithText("Next").performClick()
        currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals("onboarding_height", currentRoute)
    }

    @Test
    fun testValidHeightNavigation() {
        val heightField = composeTestRule.onAllNodes(hasSetTextAction()).onFirst()
        heightField.performTextInput("175")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_weight", navController.currentBackStackEntry?.destination?.route)
    }
}
