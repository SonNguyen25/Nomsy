package com.example.nomsy.ui.screens.auth

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
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
class OnboardingNameScreenTest {

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

            navController.graph = navController.createGraph(startDestination = "onboarding_name") {
                composable("onboarding_name") { OnboardingNameScreen(navController, authViewModel) }
                composable("onboarding_age") { /* Stub: Next screen */ }
            }
        }

        composeTestRule.setContent {
            OnboardingNameScreen(navController, authViewModel)
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithText("What's your name?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("name_input").assertIsDisplayed()
    }

    @Test
    fun testEmptyNameValidation() {
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_name", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testNumericNameValidation() {

        val nameField = composeTestRule.onNode(hasTestTag("name_input"))
        nameField.assertTextEquals("")
        nameField.performTextInput("John")
        nameField.assertTextEquals("John")
        nameField.performTextInput("123-")
        nameField.assertTextEquals("John")

        assertEquals("onboarding_name", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testValidNameNavigation() {
        composeTestRule.onNodeWithTag("name_input").performTextInput("John Doe")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("onboarding_age", navController.currentBackStackEntry?.destination?.route)
    }
}
