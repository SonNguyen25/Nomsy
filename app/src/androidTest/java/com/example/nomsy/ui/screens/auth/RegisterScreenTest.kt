package com.example.nomsy.ui.screens.auth

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.viewModels.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

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
            // Create a simple navigation graph for testing.
            navController.graph = navController.createGraph(startDestination = "register") {
                composable("register") { RegisterScreen(navController, authViewModel) }
                composable("onboarding_welcome") { }
                composable("login") { }
            }
        }

        // Set the content for testing.
        composeTestRule.setContent {
            RegisterScreen(navController, authViewModel)
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithTag("emailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("usernameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmPasswordField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("signInText").assertIsDisplayed()
    }

    @Test
    fun testEmptyEmailValidation() {
        composeTestRule.onNodeWithTag("nextButton").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
        }

    @Test
    fun testInvalidEmailValidation() {
        composeTestRule.onNodeWithTag("emailField").performTextInput("invalid")
        composeTestRule.onNodeWithTag("nextButton").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
         }

    @Test
    fun testEmptyUsernameValidation() {
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("nextButton").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testEmptyPasswordValidation() {
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("usernameField").performTextInput("testuser")
        composeTestRule.onNodeWithTag("nextButton").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testShortPasswordValidation() {
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("usernameField").performTextInput("testuser")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("12345")
        composeTestRule.onNodeWithTag("nextButton").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testPasswordMismatchValidation() {
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("usernameField").performTextInput("testuser")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("password123")
        composeTestRule.onNodeWithTag("confirmPasswordField").performTextInput("password456")
        composeTestRule.onNodeWithTag("nextButton").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testValidRegistrationNavigation() {
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("usernameField").performTextInput("testuser")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("password123")
        composeTestRule.onNodeWithTag("confirmPasswordField").performTextInput("password123")
        composeTestRule.onNodeWithTag("nextButton").performClick()
        assertEquals("onboarding_welcome", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testLoginNavigation() {
        composeTestRule.onNodeWithTag("signInText").performClick()
        assertEquals("login", navController.currentBackStackEntry?.destination?.route)
    }
}
