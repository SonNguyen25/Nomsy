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
import com.example.nomsy.data.local.models.User
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.IAuthViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.assertion.ViewAssertions.matches

@RunWith(AndroidJUnit4::class)
class OnboardingAgeScreenTest {

    class TestAuthViewModelWrapper : IAuthViewModel {
        private val _loginResult = MutableLiveData<Result<User>?>()
        override val loginResult: LiveData<Result<User>?> = _loginResult

        private val _isLoggedIn = MutableStateFlow(false)
        override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

        private val _registerResult = MutableLiveData<Result<User>>()
        override val registerResult: LiveData<Result<User>> = _registerResult

        private val _profileResult = MutableLiveData<Result<User>>()
        override val profileResult: LiveData<Result<User>> = _profileResult

        var loginUsername: String? = null
        var loginPassword: String? = null
        var loginCalled: Boolean = false

        fun setLoginResult(result: Result<User>?) {
            _loginResult.postValue(result)
        }

        override fun login(username: String, password: String) {
            loginCalled = true
            loginUsername = username
            loginPassword = password
        }

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

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var authViewModel: IAuthViewModel

    @Before
    fun setUp() {
        composeTestRule.runOnUiThread {
            navController = TestNavHostController(ApplicationProvider.getApplicationContext())
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            authViewModel = TestAuthViewModelWrapper()

            navController.graph = navController.createGraph(startDestination = "onboarding_age") {
                composable("onboarding_age") { OnboardingAgeScreen(navController, authViewModel) }
                composable("onboarding_height") {
                    Text("Onboarding Height Screen")
                }
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
        // Click the "Next" button to trigger the Toast
        composeTestRule.onNodeWithText("Next").performClick()
        // Verify route didn't change
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
