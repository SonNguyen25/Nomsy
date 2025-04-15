package com.example.nomsy.ui.screens.auth

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.data.local.models.User
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.IAuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationCompleteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var testAuthViewModel: TestAuthViewModelWrapper


    class TestAuthViewModelWrapper : IAuthViewModel {
        private val _registerResult = androidx.lifecycle.MutableLiveData<Result<User>>()
        override val registerResult = _registerResult as androidx.lifecycle.LiveData<Result<User>>

        // Other properties we don't need for this screen:
        override val loginResult = androidx.lifecycle.MutableLiveData<Result<User>?>()
        override val isLoggedIn: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
        override val profileResult = androidx.lifecycle.MutableLiveData<Result<User>>()

        // Custom setter for testing purposes:
        fun setRegisterResult(result: Result<User>?) {
            _registerResult.postValue(result)
        }

        override fun login(username: String, password: String) {}
        override fun getCurrentUsername(): String = ""
        override fun setCurrentUsername(username: String) {}
        override fun logout() {}
        override fun register(user: User) {}
        override fun fetchProfile(userId: String) {}
        override fun fetchProfileByUsername(username: String) {}
        override fun setCredentials(username: String, password: String, email: String) {}
        override fun setUserName(name: String) {}
        override fun setUserAge(age: Int) {}
        override fun setUserHeight(height: Int) {}
        override fun setUserWeight(weight: Int) {}
        override fun setUserFitnessGoal(goal: String) {}
        override fun setUserNutritionGoals(goals: Map<String, Int>) {}
        override fun getUsername(): String = ""
        override fun getPassword(): String = ""
        override fun getUserName(): String = ""
        override fun getUserAge(): Int = 0
        override fun getUserHeight(): Int = 0
        override fun getUserWeight(): Int = 0
        override fun getUserFitnessGoal(): String = ""
    }

    @Before
    fun setUp() {
        composeTestRule.runOnUiThread {
            val context: Context = ApplicationProvider.getApplicationContext()
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            // Initialize test view model instance
            testAuthViewModel = TestAuthViewModelWrapper()

            navController.graph = navController.createGraph(startDestination = "registration_complete") {
                composable("registration_complete") { RegistrationCompleteScreen(navController, testAuthViewModel) }
                composable("home") { }
            }
        }

        composeTestRule.setContent {
            RegistrationCompleteScreen(navController, testAuthViewModel)
        }
    }

    @Test
    fun testLoadingState() {
        composeTestRule.runOnUiThread {
            testAuthViewModel.setRegisterResult(Result.Loading)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("loading_indicator").assertExists()
        composeTestRule.onNodeWithText("Creating your account...").assertIsDisplayed()
    }

    @Test
    fun testSuccessState() {
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

        composeTestRule.runOnUiThread {
            testAuthViewModel.setRegisterResult(Result.Success(testUser))
        }
        composeTestRule.waitForIdle()

        // Check for success messages
        composeTestRule.onNodeWithText("Welcome to Nomsy, Test User!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your account has been created successfully.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Started").assertIsDisplayed()

        composeTestRule.onNodeWithText("Get Started").performClick()
        assertEquals("home", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testErrorState() {
        composeTestRule.runOnUiThread {
            testAuthViewModel.setRegisterResult(Result.Error(Exception("Registration failed")))
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Registration Error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Error: Registration failed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }
}
