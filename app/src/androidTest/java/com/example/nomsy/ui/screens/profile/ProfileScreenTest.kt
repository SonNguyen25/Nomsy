package com.example.nomsy.ui.screens.profile

import android.app.Application
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
import com.example.nomsy.viewModels.ProfileViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private val app: Application = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        composeTestRule.runOnUiThread {
            navController = TestNavHostController(app).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
                graph = createGraph(startDestination = "profile") {
                    composable("profile") {
                        ProfileScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            profileViewModel = profileViewModel
                        )
                    }
                    composable("edit_profile") { /* stub */ }
                    composable("login")       { /* stub */ }
                }
            }

            authViewModel = AuthViewModel(app)
            profileViewModel = ProfileViewModel(app)
        }

        composeTestRule.setContent {
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
            LaunchedEffect(isLoggedIn) {
                if (!isLoggedIn) {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel
            )
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()
        // no edit FAB
        composeTestRule.onNodeWithContentDescription("Edit Profile").assertDoesNotExist()
        // no error text
        composeTestRule.onNodeWithText("Error loading profile").assertDoesNotExist()
    }

    @Test
    fun testLoadingState() {
        composeTestRule.runOnUiThread {
            (profileViewModel.profile as MutableLiveData<Result<User>>)
                .postValue(Result.Loading)
        }
        composeTestRule.onNodeWithTag("loading_indicator").assertExists()
    }

    @Test
    fun testErrorState() {
        composeTestRule.runOnUiThread {
            (profileViewModel.profile as MutableLiveData<Result<User>>)
                .postValue(Result.Error(Exception("Test error")))
        }
        composeTestRule.onNodeWithText("Error loading profile").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Edit Profile").assertDoesNotExist()
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
            (profileViewModel.profile as MutableLiveData<Result<User>>)
                .postValue(Result.Success(testUser))
        }
//        profileViewModel.fetchByUsername("testuser")

        // user info
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("key-value").fetchSemanticsNodes().size == 14
        }
        composeTestRule.onNodeWithTag("name").assertIsDisplayed()

        composeTestRule.onNodeWithTag("age").assertIsDisplayed()

//        composeTestRule.onNodeWithText("maintain").assertIsDisplayed()
//
//        // FAB + Logout
        composeTestRule.onNodeWithContentDescription("Edit Profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
    }

    @Test
    fun testEditProfileNavigation() {
        val testUser = User(
            id = "1",
            username = "testuser",
            password = "password123",
            name = "Test User",
            age = 25,
            height = 175,
            weight = 70,
            fitness_goal = "maintain",
            nutrition_goals = emptyMap()
        )

        composeTestRule.runOnUiThread {
            (profileViewModel.profile as MutableLiveData<Result<User>>)
                .postValue(Result.Success(testUser))
        }

        // tap edit FAB
        composeTestRule.onNodeWithContentDescription("Edit Profile").performClick()
        assertEquals("edit_profile", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testLogoutFunctionality() {
        composeTestRule.runOnUiThread {
            authViewModel.setCurrentUsername("testuser")
            (profileViewModel.profile as MutableLiveData<Result<User>>)
                .postValue(Result.Success(
                    User(
                    id = "1", username = "testuser", name = "Test User",
                    password = "password",
                    age = 25,
                    height = 150,
                    weight = 43,
                    fitness_goal = "bulk",
                    nutrition_goals = emptyMap()
                )
                ))
        }

        assertEquals("testuser", authViewModel.getCurrentUsername())

        composeTestRule.onNodeWithText("Logout").performClick()

        // username cleared & nav to login
        assertEquals("", authViewModel.getCurrentUsername())
        assertEquals("login", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testProfileFetchOnUsernameChange() {

        composeTestRule.runOnUiThread {
            authViewModel.setCurrentUsername("")
        }
        composeTestRule.runOnUiThread {
            authViewModel.setCurrentUsername("testuser")
            profileViewModel.fetchByUsername("testuser")
            (profileViewModel.profile as MutableLiveData<Result<User>>)
                .postValue(Result.Loading)
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("loading_indicator").assertExists()
    }

    @Test
    fun testNoFetchWhenUsernameUnchanged() {
        // initial fetch
        composeTestRule.runOnUiThread {
            authViewModel.setCurrentUsername("testuser")
            profileViewModel.fetchByUsername("testuser")
            (profileViewModel.profile as MutableLiveData<Result<User>>)
                .postValue(Result.Loading)
        }
        // set same username again
        composeTestRule.runOnUiThread {
            authViewModel.setCurrentUsername("testuser")
        }
        // still loading
        composeTestRule.onNodeWithTag("loading_indicator").assertExists()
    }
}
