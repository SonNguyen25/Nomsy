package com.example.nomsy.ui.screens.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.MutableLiveData
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.data.local.models.User
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.IAuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.content.Context
import androidx.navigation.compose.ComposeNavigator
import androidx.lifecycle.LiveData
import androidx.navigation.compose.composable
import androidx.navigation.createGraph

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var testAuthViewModel: TestAuthViewModelWrapper

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

    @Before
    fun setUp() {
        composeTestRule.runOnUiThread {
            val context = ApplicationProvider.getApplicationContext<Context>()
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            navController.graph = navController.createGraph(startDestination = "login") {
                composable("login") { /* Test doesn't need content */ }
                composable("register") { /* Test doesn't need content */ }
                composable("home") { /* Test doesn't need content */ }
            }

            testAuthViewModel = TestAuthViewModelWrapper()
        }

        composeTestRule.setContent {
            LoginScreen(navController = navController, authViewModel = testAuthViewModel)
        }
    }

    @Test
    fun testLoginScreenInitialState() {
        composeTestRule.onNodeWithText("NOMSY").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? Sign up!").assertIsDisplayed()
    }

    @Test
    fun testEmptyUsernameShowsErrorToast() {
        composeTestRule.onNodeWithText("Sign In").performClick()
        assert(!testAuthViewModel.loginCalled)
    }

    @Test
    fun testEmptyPasswordShowsErrorToast() {
        composeTestRule.onNode(hasSetTextAction() and hasText("Username"))
            .performTextInput("testuser")
        composeTestRule.onNodeWithText("Sign In").performClick()
        assert(!testAuthViewModel.loginCalled)
    }

    @Test
    fun testValidLoginAttempt() {
        composeTestRule.onNode(hasSetTextAction() and hasText("Username"))
            .performTextInput("testuser")
        composeTestRule.onNode(hasSetTextAction() and hasText("Password"))
            .performTextInput("password123")
        composeTestRule.onNodeWithText("Sign In").performClick()

        assert(testAuthViewModel.loginCalled)
        assert(testAuthViewModel.loginUsername == "testuser")
        assert(testAuthViewModel.loginPassword == "password123")
    }

    @Test
    fun testPasswordVisibilityToggle() {
        composeTestRule.onNode(hasText("Password")).assertExists()
        composeTestRule.onNode(hasContentDescription("Show password")).performClick()
        composeTestRule.onNode(hasContentDescription("Hide password")).assertExists()
        composeTestRule.onNode(hasContentDescription("Hide password")).performClick()
        composeTestRule.onNode(hasContentDescription("Show password")).assertExists()
    }

    @Test
    fun testNavigateToRegisterScreen() {
        composeTestRule.onNodeWithText("Don't have an account? Sign up!").performClick()
        composeTestRule.waitForIdle()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testSuccessfulLoginNavigatesToHomeScreen() {
        val testUser = User(
            id = "1",
            username = "testuser",
            password = "password123",
            name = "Test User",
            age = 30,
            height = 175,
            weight = 70,
            fitness_goal = "Maintain",
            nutrition_goals = mapOf(
                "calories" to 2000,
                "protein" to 120,
                "carbs" to 250,
                "fat" to 65
            )
        )

        composeTestRule.runOnUiThread {
            testAuthViewModel.setLoginResult(Result.Success(testUser))
        }

        composeTestRule.waitForIdle()
        assertEquals("home", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testLoginError() {
        composeTestRule.runOnUiThread {
            testAuthViewModel.setLoginResult(Result.Error(Exception("Invalid credentials")))
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Error: Invalid credentials").assertIsDisplayed()
    }

    @Test
    fun testLoginLoading() {
        composeTestRule.runOnUiThread {
            testAuthViewModel.setLoginResult(Result.Loading)
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNode(hasTestTag("loading_indicator")).assertExists()
    }
}