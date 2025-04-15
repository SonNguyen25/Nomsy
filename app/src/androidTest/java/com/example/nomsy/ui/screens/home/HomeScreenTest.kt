package com.example.nomsy.ui.screens.home

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.data.repository.AuthRepository
import com.example.nomsy.data.repository.MealTrackerRepository
import com.example.nomsy.ui.screens.HomeScreen
import com.example.nomsy.ui.screens.auth.LoginScreenTest.TestAuthViewModelWrapper
import com.example.nomsy.viewModels.AuthViewModel
import com.example.nomsy.viewModels.HomeViewModel
import com.example.nomsy.viewModels.ProfileViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var profileViewModel: ProfileViewModel

    // Fake repositories
    private lateinit var fakeMealRepository: FakeMealTrackerRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var mockMealRepository: MealTrackerRepository
    private lateinit var mockAuthRepository: AuthRepository

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
            authViewModel = TestAuthViewModelWrapper()
        }

        composeTestRule.setContent {
            HomeScreen(
                navController = navController, authViewModel = authViewModel,
                viewModel = homeViewModel,
                profileViewModel = profileViewModel
            )
        }
        val context = ApplicationProvider.getApplicationContext<Context>()
        val application = ApplicationProvider.getApplicationContext<Application>()

        // mock data
        val nutritionGoals = mapOf(
            "water" to 2,
            "calories" to 2000,
            "protein" to 100,
            "carbs" to 250,
            "fat" to 70
        )

        val testUser = User(
            id = "1",
            username = "j",
            nutrition_goals = nutritionGoals,
            password = "password",
            name = "Joshua",
            age = 90,
            height = 169,
            weight = 69,
            fitness_goal = "maintain"
        )

        val dailySummary = DailySummaryEntity(
            date = "2025-04-12",
            totalCalories = 1500,
            totalProtein = 75,
            totalCarbs = 180,
            totalFat = 50,
            waterLiters = 1.5,
            lastUpdated = System.currentTimeMillis()
        )

        val breakfast = listOf(
            MealItem(
                food_name = "Scrambled Eggs",
                calories = 220,
                carbs = 2,
                protein = 14,
                fat = 16
            ),
            MealItem(
                food_name = "Whole Wheat Toast",
                calories = 90,
                carbs = 15,
                protein = 3,
                fat = 1
            )
        )

        val lunch = listOf(
            MealItem(
                food_name = "Grilled Chicken Salad",
                calories = 350,
                carbs = 10,
                protein = 32,
                fat = 18
            )
        )
        val dinner = listOf(
            MealItem(
                food_name = "Banana",
                calories = 350,
                carbs = 10,
                protein = 32,
                fat = 18
            )
        )

        val mealsByType = mapOf(
            "breakfast" to breakfast,
            "lunch" to lunch,
            "dinner" to dinner
        )
        fakeMealRepository = FakeMealTrackerRepository(dailySummary, mealsByType)
        fakeAuthRepository = FakeAuthRepository(testUser)

        homeViewModel = HomeViewModel(application)
        authViewModel = AuthViewModel(application)
        profileViewModel = ProfileViewModel(application)

        // inject into HomeViewModel
        val homeRepoField = HomeViewModel::class.java.getDeclaredField("mealRepository")
        homeRepoField.isAccessible = true
        homeRepoField.set(homeViewModel, fakeMealRepository)

        // inject into AuthViewModel
        val authRepoField = AuthViewModel::class.java.getDeclaredField("repository")
        authRepoField.isAccessible = true
        authRepoField.set(authViewModel, fakeAuthRepository)

        // inject into ProfileViewModel
        val profileRepoField = ProfileViewModel::class.java.getDeclaredField("repo")
        profileRepoField.isAccessible = true
        profileRepoField.set(profileViewModel, fakeAuthRepository)

        // force logged in state by injecting Auth
        val usernameField = AuthViewModel::class.java.getDeclaredField("_currentUsername")
        usernameField.isAccessible = true
        usernameField.set(authViewModel, "j")


        composeTestRule.setContent {
            HomeScreen(
                navController = navController,
                viewModel = homeViewModel,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel
            )
        }
    }

    @Test
    fun testCalorieDisplayIsCorrect() {
        // Wait for async operations to complete
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Calories").assertIsDisplayed()
        composeTestRule.onNodeWithText("1500 kcal").assertIsDisplayed()
        composeTestRule.onNodeWithText("out of 2000").assertIsDisplayed()
    }

    @Test
    fun testMacronutrientCirclesAreDisplayed() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Protein").assertIsDisplayed()
        composeTestRule.onNodeWithText("75g").assertIsDisplayed()
        composeTestRule.onNodeWithText("Carbs").assertIsDisplayed()
        composeTestRule.onNodeWithText("180g").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fat").assertIsDisplayed()
        composeTestRule.onNodeWithText("50g").assertIsDisplayed()
    }
}

class FakeAuthRepository(testUser: User) {

}

class FakeMealTrackerRepository(
    dailySummary: DailySummaryEntity,
    mealsByType: Map<String, List<MealItem>>
) {

}
