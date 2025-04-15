package com.example.nomsy.ui.screens.auth

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.data.local.entities.User
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.AuthViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationCompleteScreenTest {

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

            navController.graph =
                navController.createGraph(startDestination = "registration_complete") {
                    composable("registration_complete") {
                        RegistrationCompleteScreen(navController, authViewModel)
                    }
                    composable("home") { }
                }
        }

        composeTestRule.setContent {
            RegistrationCompleteScreen(navController, authViewModel)
        }
    }

    @Test
    fun testLoadingState() {
        composeTestRule.runOnUiThread {
            (authViewModel.registerResult as? MutableLiveData<Result<User>>)
                ?.postValue(Result.Loading)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("loading_indicator").assertExists()
        composeTestRule.onNodeWithText("Creating your account...").assertIsDisplayed()
    }

    @Test
    fun testSuccessState() {
        // Create a test user.
        val testUser = User(
            id = "1",
            username = "testuser",
            password = "password123",
            name = "Test User",
            age = 25,
            height = 175,
            weight = 70,
            fitness_goal = "maintain",
            nutrition_goals = mapOf(
                "calories" to 2000,
                "protein" to 120,
                "carbs" to 250,
                "fat" to 65
            )
        )
        // Simulate a successful registration.
        composeTestRule.runOnUiThread {
            (authViewModel.registerResult as? MutableLiveData<Result<User>>)
                ?.postValue(Result.Success(testUser))
        }
        composeTestRule.waitForIdle()

        // Verify that the success messages are displayed.
        composeTestRule.onNodeWithText("Welcome to Nomsy, Test User!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your account has been created successfully.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Started").assertIsDisplayed()

        composeTestRule.onNodeWithText("Get Started").performClick()
        assertEquals("home", navController.currentBackStackEntry?.destination?.route)


    }

    @Test
    fun testErrorState() {
        composeTestRule.runOnUiThread {
            (authViewModel.registerResult as? MutableLiveData<Result<User>>)
                ?.postValue(Result.Error(Exception("Registration failed")))
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Registration Error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Error: Registration failed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }
}
