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
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var testAuthViewModel: TestAuthViewModelWrapper

    // Test implementation of IAuthViewModel
    class TestAuthViewModelWrapper : IAuthViewModel {
        override val registerResult = androidx.lifecycle.MutableLiveData<Result<User>>()
        override val loginResult = androidx.lifecycle.MutableLiveData<Result<User>?>()
        override val isLoggedIn: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
        override val profileResult = androidx.lifecycle.MutableLiveData<Result<User>>()

        // The following functions are not needed for input validation tests.
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
            testAuthViewModel = TestAuthViewModelWrapper()
            // Create a navigation graph for registration.
            navController.graph = navController.createGraph(startDestination = "register") {
                composable("register") { RegisterScreen(navController, testAuthViewModel) }
                composable("onboarding_welcome") { }
                composable("login") { }
            }
        }

        composeTestRule.setContent {
            RegisterScreen(navController, testAuthViewModel)
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithText("NOMSY").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
        composeTestRule.onNodeWithText("Already have an account?").assertIsDisplayed()
    }

    @Test
    fun testEmptyEmailValidation() {
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
        composeTestRule.onNodeWithText("Email").assertTextEquals("")
    }

    @Test
    fun testInvalidEmailValidation() {
        composeTestRule.onNodeWithText("Email").performTextInput("invalid")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
        composeTestRule.onNodeWithText("Email").assertTextEquals("")
    }

    @Test
    fun testEmptyUsernameValidation() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testEmptyPasswordValidation() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testShortPasswordValidation() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Password").performTextInput("12345")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testPasswordMismatchValidation() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password456")
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testValidRegistrationNavigation() {
        // Input valid registration details.
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Next").performClick()
        // Verify that navigation occurs to onboarding_welcome.
        assertEquals("onboarding_welcome", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testLoginNavigation() {
        composeTestRule.onNodeWithText("Sign in!").performClick()
        assertEquals("login", navController.currentBackStackEntry?.destination?.route)
    }
}
