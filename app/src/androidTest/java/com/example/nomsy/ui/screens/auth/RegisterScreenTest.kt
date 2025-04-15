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
            // Instantiate the real AuthViewModel using the Application context.
            authViewModel = AuthViewModel(context as Application)
            // Create a simple navigation graph for testing.
            navController.graph = navController.createGraph(startDestination = "register") {
                composable("register") { RegisterScreen(navController, authViewModel) }
                composable("onboarding_welcome") { /* Stub screen */ }
                composable("login") { /* Stub screen */ }
            }
        }

        // Set the content for testing.
        composeTestRule.setContent {
            RegisterScreen(navController, authViewModel)
        }
    }

    @Test
    fun testInitialState() {
        // Using test tags to verify that each key element is displayed.
        composeTestRule.onNodeWithTag("emailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("usernameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmPasswordField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("signInText").assertIsDisplayed()
    }

    @Test
    fun testEmptyEmailValidation() {
        // Click "Next" without inputting email.
        composeTestRule.onNodeWithTag("nextButton").performClick()
        // Expect to remain in the register screen.
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
        // Verify that the email field is cleared.
        composeTestRule.onNodeWithTag("emailField").assertTextEquals("")
    }

    @Test
    fun testInvalidEmailValidation() {
        // Input an invalid email.
        composeTestRule.onNodeWithTag("emailField").performTextInput("invalid")
        composeTestRule.onNodeWithTag("nextButton").performClick()
        // Expect to remain on the registration screen.
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
        // Verify that the email field is cleared after invalid input.
        composeTestRule.onNodeWithTag("emailField").assertTextEquals("")
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
        // Provide valid registration details.
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("usernameField").performTextInput("testuser")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("password123")
        composeTestRule.onNodeWithTag("confirmPasswordField").performTextInput("password123")
        composeTestRule.onNodeWithTag("nextButton").performClick()
        // Verify that the navigation goes to the "onboarding_welcome" screen.
        assertEquals("onboarding_welcome", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testLoginNavigation() {
        // Click on the "Sign in!" text using its test tag.
        composeTestRule.onNodeWithTag("signInText").performClick()
        // Verify navigation to the "login" screen.
        assertEquals("login", navController.currentBackStackEntry?.destination?.route)
    }
}
