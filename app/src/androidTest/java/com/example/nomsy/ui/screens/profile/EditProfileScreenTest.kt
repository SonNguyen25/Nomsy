package com.example.nomsy.ui.screens.profile

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.data.local.entities.User
import com.example.nomsy.data.remote.UpdateProfileRequest
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.AuthViewModel
import com.example.nomsy.viewModels.IProfileViewModel
import kotlinx.coroutines.Job
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var profileViewModel: IProfileViewModel
    private val app: Application = ApplicationProvider.getApplicationContext()

    class FakeProfileViewModel : IProfileViewModel {
        private val _profile = MutableLiveData<Result<User>>()
        private val _updateResult = MutableLiveData<Result<Unit>>()

        private val testUser = User(
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
                "fat" to 65,
                "water" to 2
            )
        )
        override val profile: LiveData<Result<User>> = _profile
        override val updateResult: LiveData<Result<User>?> =
            _updateResult as LiveData<Result<User>?>

        override fun fetchByUsername(username: String): Job {
            _profile.postValue(Result.Loading)

            return Job().apply {
                Handler(Looper.getMainLooper()).postDelayed({
                    _profile.postValue(Result.Success(testUser))
                }, 1000)
            }
        }

        override fun updateProfile(username: String, updateRequest: UpdateProfileRequest): Job {
            _updateResult.postValue(Result.Loading)
            return Job().apply {
                complete()
                Handler(Looper.getMainLooper()).postDelayed({
                    _updateResult.postValue(Result.Success(Unit))
                }, 300)
            }
        }

        override fun clearUpdateState() {
            _updateResult.postValue(null)

        }
    }


    @Before
    fun setUp() {
        composeTestRule.runOnUiThread {
            // Initialize ViewModels first
            authViewModel = AuthViewModel(app)
            profileViewModel = FakeProfileViewModel()

            // Set initial state BEFORE composing the screen
            authViewModel.setCurrentUsername("testuser")
//            (profileViewModel.profile as MutableLiveData<Result<User>>)
//                .postValue(Result.Success(testUser))

            // Now set up navigation with the pre-populated ViewModels
            navController = TestNavHostController(app).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
                graph = createGraph(startDestination = "profile") {
                    composable("profile") { }
                    composable("edit_profile") {
                    }
                }
            }
            navController.navigate("edit_profile")

        }

        composeTestRule.setContent {
            EditProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                authViewModel = authViewModel
            )
        }
    }

    @Test
    fun testInitialState() {
        // Verify header
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }
//        composeTestRule.waitUntil(timeoutMillis = 5_000) {
        composeTestRule.onAllNodesWithText("Edit Profile").fetchSemanticsNodes().isNotEmpty()
//        }
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()

        // Verify form fields are populated with user data
        composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()


        composeTestRule.onNodeWithText("25").assertIsDisplayed()
        composeTestRule.onNodeWithText("175").assertIsDisplayed()
        composeTestRule.onNodeWithText("70").assertIsDisplayed()
        composeTestRule.onNodeWithText("2000").assertIsDisplayed()
        composeTestRule.onNodeWithText("120").assertIsDisplayed()
        composeTestRule.onNodeWithText("250").assertIsDisplayed()
        composeTestRule.onNodeWithText("65").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()

        // Verify fitness goal selection
        composeTestRule.onNodeWithTag("maintain").assertIsSelected()
    }

    @Test
    fun testLoadingState() {
        composeTestRule.runOnUiThread {
            // Trigger loading state
            (profileViewModel as FakeProfileViewModel).fetchByUsername("testuser")
        }


        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("loading").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()

        // Wait for loading to complete (success state)
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testErrorState() {
        composeTestRule.runOnUiThread {
            // Need to trigger error state
            val _profile = FakeProfileViewModel::class.java
                .getDeclaredField("_profile")
                .apply { isAccessible = true }
                .get(profileViewModel as FakeProfileViewModel) as MutableLiveData<Result<User>>

            _profile.postValue(Result.Error(Exception("Test error")))
        }

        // Wait until error message appears
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasText("Error loading profile", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }


        composeTestRule.onNode(hasText("Error loading profile", substring = true))
            .assertIsDisplayed()

    }

    @Test
    fun testBackNavigation() {
        // Wait for the screen to load first
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertEquals("profile", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testFormValidation() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }
        // Test numeric validation
        composeTestRule.onNodeWithText("Age").performTextInput("abc")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Age").assertExists("25")

        composeTestRule.onNodeWithText("Height (cm)").performTextInput("abc")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Height (cm)").assertExists("175")

        composeTestRule.onNodeWithText("Weight (kg)").performTextInput("abc")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Weight (kg)").assertExists("70")

        // Test valid numeric input
        composeTestRule.onNodeWithText("Age").performTextReplacement("30")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Age").assertExists("30")
    }

    @Test
    fun testFitnessGoalSelection() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Cut").performClick()
        composeTestRule.onNodeWithText("Cut").assertIsSelected()

        composeTestRule.onNodeWithText("Bulk").performClick()
        composeTestRule.onNodeWithText("Bulk").assertIsSelected()

        composeTestRule.onNodeWithText("Maintain").performClick()
        composeTestRule.onNodeWithText("Maintain").assertIsSelected()
    }

    @Test
    fun testRandomGoalSelection() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("lucky-text").performClick()
        val goals = listOf("cut", "maintain", "bulk")
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            goals.any {
                try {
                    composeTestRule.onNodeWithTag(it).assertIsSelected()
                    true
                } catch (_: AssertionError) {
                    false
                }
            }
        }
    }

    @Test
    fun testUpdateProfileSuccess() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }

        // Modify some fields
        composeTestRule.onNodeWithText("Name").performTextReplacement("New Name")
        composeTestRule.onNodeWithText("Age").performTextReplacement("30")
        composeTestRule.onNodeWithText("Bulk").performClick()

        // Submit form
        composeTestRule.onNodeWithText("Update").performClick()

        // Verify navigation back
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            navController.currentBackStackEntry?.destination?.route == "profile"
        }
    }


    @Test
    fun testUpdateProfileLoading() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Update").performClick()

        // Loading is displayed when update is triggered
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("loading").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun testUpdateProfileError() {
        // Wait for form to load
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Name").performTextReplacement("New Name")
        composeTestRule.onNodeWithText("Age").performTextReplacement("30")
        composeTestRule.onNodeWithText("Bulk").performClick()

        composeTestRule.onNodeWithText("Update").performClick()

        // Prepare for the update with error
        composeTestRule.waitForIdle()
        val _updateResult = FakeProfileViewModel::class.java
            .getDeclaredField("_updateResult")
            .apply { isAccessible = true }
            .get(profileViewModel as FakeProfileViewModel) as MutableLiveData<Result<Unit>>

        _updateResult.postValue(Result.Error(Exception("Update failed")))
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasText("Update failed", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testNutritionGoalsValidation() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }
        // Test numeric validation for nutrition fields
        composeTestRule.onNodeWithText("Calories").performTextInput("abc")
        composeTestRule.onNodeWithText("Calories").assertExists("2000")

        composeTestRule.onNodeWithText("Protein").performTextInput("abc")
        composeTestRule.onNodeWithText("Protein").assertExists("120")

        composeTestRule.onNodeWithText("Carbs").performTextInput("abc")
        composeTestRule.onNodeWithText("Carbs").assertExists("250")

        composeTestRule.onNodeWithText("Fat").performTextInput("abc")
        composeTestRule.onNodeWithText("Fat").assertExists("65")

        composeTestRule.onNodeWithText("Water (L)").performTextInput("abc")
        composeTestRule.onNodeWithText("Water (L)").assertExists("2")
    }

    @Test
    fun testEmptyFieldsHandling() {
        // Wait for form to load
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Test User").fetchSemanticsNodes().isNotEmpty()
        }

        // Clear fields
        composeTestRule.onNodeWithText("Test User").performTextReplacement("")
        composeTestRule.onNodeWithText("25").performTextReplacement("")
        composeTestRule.onNodeWithText("175").performTextReplacement("")
        composeTestRule.onNodeWithText("70").performTextReplacement("")
        composeTestRule.onNodeWithText("2000").performTextReplacement("")
        composeTestRule.onNodeWithText("120").performTextReplacement("")
        composeTestRule.onNodeWithText("250").performTextReplacement("")
        composeTestRule.onNodeWithText("65").performTextReplacement("")
        composeTestRule.onNodeWithText("2").performTextReplacement("")

        // Submit form
        composeTestRule.onNodeWithText("Update").performClick()

        // Wait for navigation back
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            navController.currentBackStackEntry?.destination?.route == "profile"
        }
    }

}