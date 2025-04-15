package com.example.nomsy.ui.screens.auth

import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.viewModels.AuthViewModel
import com.example.nomsy.viewModels.IAuthViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingAgeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var authViewModel: IAuthViewModel

    @Before
    fun setUp() {
        composeTestRule.runOnUiThread {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            authViewModel = AuthViewModel(context as android.app.Application)
            navController.graph = navController.createGraph(startDestination = "onboarding_age") {
                composable("onboarding_age") { OnboardingAgeScreen(navController, authViewModel) }
                composable("onboarding_height") { Text("Onboarding Height Screen") }
            }
        }

        composeTestRule.setContent {
            OnboardingAgeScreen(navController, authViewModel)
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithText("What's your age?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("age_input")
            .assertExists("The text field with tag 'age_input' was not found.")
        composeTestRule.onNodeWithTag("age_input").printToLog("DEBUG")
    }

    @Test
    fun testEmptyAgeValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(
            "Navigation occurred when it shouldn't have",
            initialRoute,
            navController.currentBackStackEntry?.destination?.route
        )
        composeTestRule.onNodeWithTag("age_input")
            .assertExists()
            .performTextInput("")
    }

    @Test
    fun testInvalidAgeValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithTag("age_input").performTextInput("abc")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(
            "Navigation occurred when it shouldn't have",
            initialRoute,
            navController.currentBackStackEntry?.destination?.route
        )
        composeTestRule.onNodeWithTag("age_input")
            .assertExists()
            .performTextInput("")
    }

    @Test
    fun testAgeTooLowValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithTag("age_input").performTextInput("12")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(
            "Navigation occurred when it shouldn't have",
            initialRoute,
            navController.currentBackStackEntry?.destination?.route
        )
        composeTestRule.onNodeWithTag("age_input")
            .assertExists()
            .performTextInput("")
    }

    @Test
    fun testAgeTooHighValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithTag("age_input").performTextInput("121")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(
            "Navigation occurred when it shouldn't have",
            initialRoute,
            navController.currentBackStackEntry?.destination?.route
        )
        composeTestRule.onNodeWithTag("age_input")
            .assertExists()
            .performTextInput("")
    }

    @Test
    fun testValidAgeNavigation() {
        composeTestRule.onNodeWithTag("age_input").performTextInput("25")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_height", navController.currentBackStackEntry?.destination?.route)
    }
}
