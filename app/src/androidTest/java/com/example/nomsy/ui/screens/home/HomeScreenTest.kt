package com.example.nomsy.ui.screens.home


import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.data.remote.UpdateProfileRequest
import com.example.nomsy.ui.screens.HomeScreen
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.IAuthViewModel
import com.example.nomsy.viewModels.IHomeViewModel
import com.example.nomsy.viewModels.IProfileViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Minimal implementation of required dependencies

    // Simple auth view model that just returns a test username
    class SimpleAuthViewModel : IAuthViewModel {
        override val loginResult = MutableLiveData<Result<User>?>(null)
        override val isLoggedIn = MutableStateFlow(true).asStateFlow()
        override val registerResult = MutableLiveData<Result<User>>()
        override val profileResult = MutableLiveData<Result<User>>()

        override fun getCurrentUsername() = "testuser"
        override fun setCurrentUsername(username: String) {}
        override fun login(username: String, password: String) {}
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
        override fun getUsername() = ""
        override fun getPassword() = ""
        override fun getUserName() = ""
        override fun getUserAge() = 0
        override fun getUserHeight() = 0
        override fun getUserWeight() = 0
        override fun getUserFitnessGoal() = ""
    }

    // Simple home view model with minimal test data
    class SimpleHomeViewModel : IHomeViewModel {
        // Nutrition data
        private val _nutritionTotals = MutableLiveData<Result<DailySummaryEntity?>>(
            Result.Success(
                DailySummaryEntity(
                    date = "2025-04-14",
                    totalCalories = 1500,
                    totalProtein = 100,
                    totalCarbs = 150,
                    totalFat = 40,
                    waterLiters = 2.0
                )
            )
        )
        override val nutritionTotals: LiveData<Result<DailySummaryEntity?>> = _nutritionTotals

        // Meal data
        private val _mealsByType = MutableLiveData<Result<Map<String, List<MealItem>>>>(
            Result.Success(

                mapOf(
                    "breakfast" to listOf(
                        MealItem("Scrambled Eggs", 220, 2, 14, 16),
                        MealItem("Whole Wheat Toast", 90, 15, 3, 1)
                    ),
                    "lunch" to listOf(
                        MealItem("Grilled Chicken Salad", 350, 10, 32, 18)
                    ),
                    "dinner" to listOf(
                        MealItem("Salmon", 300, 0, 25, 20),
                        MealItem("Broccoli", 55, 10, 4, 0)
                    )
                )
            )
        )

        override val mealsByType: LiveData<Result<Map<String, List<MealItem>>>> = _mealsByType

        // Water intake
        private val _waterIntake = MutableStateFlow(2.0)
        override val waterIntake: StateFlow<Double> = _waterIntake.asStateFlow()

        // Date
        override val selectedDate = MutableStateFlow(14)

        // Methods
        override fun incrementDate() {
            if (selectedDate.value < 14) selectedDate.value += 1
        }

        override fun decrementDate() {
            if (selectedDate.value > 11) selectedDate.value -= 1
        }

        override fun updateWaterIntake(date: String, newWaterIntake: Double) {
            _waterIntake.value = newWaterIntake
        }

        override fun deleteMeal(date: String, foodName: String) {
            // Do nothing for test
        }

        override fun refreshData() {
            TODO("Not yet implemented")
        }
    }

    // Simple profile view model that returns a test user profile
    class SimpleProfileViewModel : IProfileViewModel {
        private val testUser = User(
            id = "123",
            username = "testuser",
            name = "Test User",
            age = 30,
            height = 180,
            weight = 75,
            fitness_goal = "weight loss",
            nutrition_goals = mapOf(
                "calories" to 2000,
                "protein" to 150,
                "carbs" to 200,
                "fat" to 60,
                "water" to 3
            ),
            password = "password"
        )

        private val _profile = MutableLiveData<Result<User>>(Result.Success(testUser))
        override val profile: LiveData<Result<User>> = _profile

        private val _updateResult = MutableLiveData<Result<User>?>(null)
        override val updateResult: MutableLiveData<Result<User>?> = _updateResult

        override fun fetchByUsername(username: String): Job {
            // Already set in constructor
            return kotlinx.coroutines.CompletableDeferred<Unit>().also { it.complete(Unit) }
        }

        override fun updateProfile(username: String, req: UpdateProfileRequest): Job {
            // Do nothing for test
            return kotlinx.coroutines.CompletableDeferred<Unit>().also { it.complete(Unit) }
        }

        override fun clearUpdateState() {
            _updateResult.value = null
        }
    }

    private lateinit var navController: TestNavHostController
    private lateinit var authViewModel: SimpleAuthViewModel
    private lateinit var homeViewModel: SimpleHomeViewModel
    private lateinit var profileViewModel: SimpleProfileViewModel

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

            // Initialize simple view models
            authViewModel = SimpleAuthViewModel()
            homeViewModel = SimpleHomeViewModel()
            profileViewModel = SimpleProfileViewModel()
        }
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
    fun displayHomeScreen() {
        // Just display the home screen - no assertions

        // render the HomeScreen :(
    }

    /**
     *  start testing here
     * */

    @Test
    fun displayDateSelector() {
        // Check date selector is displayed
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("4/14/2025").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Previous day").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Next day").assertIsDisplayed()
    }

    @Test
    fun changeDatePrev() {
        // Check date selector is displayed
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("4/14/2025").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Previous day").performClick()
        composeTestRule.onNodeWithText("4/13/2025").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Previous day").performClick()
        composeTestRule.onNodeWithText("4/12/2025").assertIsDisplayed()
    }

    @Test
    fun changeDateNext() {
        // Check date selector is displayed
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("4/14/2025").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Previous day").performClick()
        composeTestRule.onNodeWithText("4/13/2025").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Next day").performClick()
        composeTestRule.onNodeWithText("4/14/2025").assertIsDisplayed()
    }

    @Test
    fun displayCalorieTotal() {
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("1500 kcal").assertIsDisplayed()
        composeTestRule.onNodeWithText("out of 2000").assertIsDisplayed()
    }

    @Test
    fun displayNutritionLabels() {
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Calories").assertIsDisplayed()
        composeTestRule.onNodeWithText("Protein").assertIsDisplayed()
        composeTestRule.onNodeWithText("Carbs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fat").assertIsDisplayed()
    }

    @Test
    fun displayNutritionNumbers() {
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        // Check nutrition circles are displayed
        composeTestRule.onAllNodes(hasText("50g", substring = true), useUnmergedTree = true)
            .assertCountEquals(2)
        composeTestRule.onNodeWithText("20g").assertIsDisplayed()
    }

    @Test
    fun displayWaterSection() {
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        // Check water intake is displayed
        composeTestRule.onNodeWithText("Water Intake").assertIsDisplayed()
        composeTestRule.onNodeWithText("2.0 L").assertIsDisplayed()
        composeTestRule.onNodeWithText("2.0 / 3.0 L").assertIsDisplayed()
    }

    @Test
    fun displayMealTypeLabels() {
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        // Check meal sections are displayed
        composeTestRule.onNodeWithText("Breakfast").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lunch").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dinner").assertIsDisplayed()
    }

    @Test
    fun displayMeals() {
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        // Check meal items are displayed
        composeTestRule.onNodeWithText("Scrambled Eggs").assertIsDisplayed()
    }

    @Test
    fun displayMealsCalorie() {
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        // Check meal items are displayed
        composeTestRule.onNodeWithText("220 kcal").assertIsDisplayed()
    }

    @Test
    fun displayAddFoodButton() {
        // Check add food button is displayed
        composeTestRule.onNodeWithContentDescription("Add Food").assertIsDisplayed()
    }

    // testing opening the dialog
    @Test
    fun displayWaterIntakeDialog() {
        composeTestRule.onNodeWithText("2.0 L").performClick()
        composeTestRule.onNodeWithContentDescription("Increase").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Decrease").assertIsDisplayed()
        composeTestRule.onNodeWithText("2.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Update Water Intake").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Update").assertIsDisplayed()
    }

}
