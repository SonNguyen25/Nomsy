//package com.example.nomsy.ui.navigation
//
//import android.content.Context
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithContentDescription
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.navigation.compose.ComposeNavigator
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.createGraph
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.example.nomsy.data.local.entities.DailySummaryEntity
//import com.example.nomsy.data.local.models.Recipe
//import com.example.nomsy.data.local.models.User
//import com.example.nomsy.data.remote.MealItem
//import com.example.nomsy.data.remote.UpdateProfileRequest
//import com.example.nomsy.ui.screens.auth.LoginScreen
//import com.example.nomsy.utils.Result
//import com.example.nomsy.viewModels.IAuthViewModel
//import com.example.nomsy.viewModels.IHomeViewModel
//import com.example.nomsy.viewModels.IProfileViewModel
//import com.example.nomsy.viewModels.IRecipeViewModel
//import kotlinx.coroutines.CompletableDeferred
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class NomsyAppNavHostTest {
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    // Simple auth view model that just returns a test username
//    class SimpleAuthViewModel : IAuthViewModel {
//        private val _loginResult = MutableLiveData<Result<User>?>()
//        override val loginResult: LiveData<Result<User>?> = _loginResult
//
//        private val _isLoggedIn = MutableStateFlow(false)
//        override val isLoggedIn = MutableStateFlow(false).asStateFlow()
//
//        private val _registerResult = MutableLiveData<Result<User>>()
//        override val registerResult: LiveData<Result<User>> = _registerResult
//
//        private val _profileResult = MutableLiveData<Result<User>>()
//        override val profileResult: LiveData<Result<User>> = _profileResult
//
//        var loginUsername: String? = null
//        var loginPassword: String? = null
//        var loginCalled: Boolean = false
//
//        fun setLoginResult(result: Result<User>?) {
//            _loginResult.postValue(result)
//        }
//
//        override fun login(username: String, password: String) {
//            loginCalled = true
//            loginUsername = username
//            loginPassword = password
//        }
//
//
//        override fun getCurrentUsername() = ""
//        override fun setCurrentUsername(username: String) {}
//        override fun logout() {}
//        override fun register(user: User) {}
//        override fun fetchProfile(userId: String) {}
//        override fun fetchProfileByUsername(username: String) {}
//        override fun setCredentials(username: String, password: String, email: String) {}
//        override fun setUserName(name: String) {}
//        override fun setUserAge(age: Int) {}
//        override fun setUserHeight(height: Int) {}
//        override fun setUserWeight(weight: Int) {}
//        override fun setUserFitnessGoal(goal: String) {}
//        override fun setUserNutritionGoals(goals: Map<String, Int>) {}
//        override fun getUsername() = ""
//        override fun getPassword() = ""
//        override fun getUserName() = ""
//        override fun getUserAge() = 0
//        override fun getUserHeight() = 0
//        override fun getUserWeight() = 0
//        override fun getUserFitnessGoal() = ""
//    }
//
//    // Simple home view model with minimal test data
//    class SimpleHomeViewModel : IHomeViewModel {
//        // Nutrition data
//        private val _nutritionTotals = MutableLiveData<Result<DailySummaryEntity?>>(
//            Result.Success(
//                DailySummaryEntity(
//                    date = "2025-04-14",
//                    totalCalories = 1500,
//                    totalProtein = 100,
//                    totalCarbs = 150,
//                    totalFat = 40,
//                    waterLiters = 2.0
//                )
//            )
//        )
//        override val nutritionTotals: LiveData<Result<DailySummaryEntity?>> = _nutritionTotals
//
//        // Meal data
//        private val _mealsByType = MutableLiveData<Result<Map<String, List<MealItem>>>>(
//            Result.Success(
//
//                mapOf(
//                    "breakfast" to listOf(
//                        MealItem("Scrambled Eggs", 220, 2, 14, 16),
//                        MealItem("Whole Wheat Toast", 90, 15, 3, 1)
//                    ),
//                    "lunch" to listOf(
//                        MealItem("Grilled Chicken Salad", 350, 10, 32, 18)
//                    ),
//                    "dinner" to listOf(
//                        MealItem("Salmon", 300, 0, 25, 20),
//                        MealItem("Broccoli", 55, 10, 4, 0)
//                    )
//                )
//            )
//        )
//
//        override val mealsByType: LiveData<Result<Map<String, List<MealItem>>>> = _mealsByType
//
//        // Water intake
//        private val _waterIntake = MutableStateFlow(2.0)
//        override val waterIntake: StateFlow<Double> = _waterIntake.asStateFlow()
//
//        // Date
//        override val selectedDate = MutableStateFlow(14)
//
//        // Methods
//        override fun incrementDate() {
//            if (selectedDate.value < 14) selectedDate.value += 1
//        }
//
//        override fun decrementDate() {
//            if (selectedDate.value > 11) selectedDate.value -= 1
//        }
//
//        override fun updateWaterIntake(date: String, newWaterIntake: Double) {
//            _waterIntake.value = newWaterIntake
//        }
//
//        override fun deleteMeal(date: String, foodName: String) {
//            // Do nothing for test
//        }
//
//        override fun refreshData() {
//            TODO("Not yet implemented")
//        }
//    }
//
//    class FakeRecipeViewModel : IRecipeViewModel {
//        // StateFlow for recipes
//        private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
//        override val recipes: StateFlow<List<Recipe>> = _recipes
//
//        // StateFlow for recipes by category
//        private val _recipesByCategory = MutableStateFlow<Map<String, List<Recipe>>>(emptyMap())
//        override val recipesByCategory: StateFlow<Map<String, List<Recipe>>> = _recipesByCategory
//
//        // Loading state if needed
//        private val _isLoading = MutableStateFlow(false)
//        val isLoading: StateFlow<Boolean> = _isLoading
//
//        // Test data
//        private val testRecipes = listOf(
//            Recipe(
//                idMeal = "1",
//                strMeal = "Spaghetti Carbonara",
//                strCategory = "Pasta",
//                strArea = "Italian",
//                strInstructions = "Cook pasta. Mix eggs, cheese, and pancetta.",
//                strMealThumb = "https://example.com/carbonara.jpg",
//                strTags = "Pasta,Italian",
//                strYoutube = "https://youtube.com/watch?v=example",
//                ingredients = listOf("hi", "hi2")
//            ),
//            Recipe(
//                idMeal = "2",
//                strMeal = "Chicken Curry",
//                strCategory = "Chicken",
//                strArea = "Indian",
//                strInstructions = "Cook chicken with curry spices and coconut milk.",
//                strMealThumb = "https://example.com/curry.jpg",
//                strTags = "Chicken,Spicy,Curry",
//                strYoutube = "https://youtube.com/watch?v=example2",
//                ingredients = listOf("ingrdient3", "ingrefdidnt4")
//            )
//        )
//
//        init {
//            // initialize with test data
//            loadAllRecipes()
//        }
//
//        // methods for controlling the fake from tests
//        fun setRecipes(recipes: List<Recipe>) {
//            _recipes.value = recipes
//            _recipesByCategory.value = recipes.groupBy { it.strCategory ?: "Uncategorized" }
//        }
//
//        fun setLoading(loading: Boolean) {
//            _isLoading.value = loading
//        }
//
//        // implement interface methods
//        override fun search(query: String) {
//            _isLoading.value = true
//
//            if (query.isEmpty()) {
//                loadAllRecipes()
//            } else {
//                // filter test recipes based on query
//                val filteredRecipes = testRecipes.filter {
//                    it.strMeal.contains(query, ignoreCase = true) ||
//                            it.strCategory?.contains(query, ignoreCase = true) == true ||
//                            it.strArea?.contains(query, ignoreCase = true) == true
//                }
//                _recipes.value = filteredRecipes
//                _recipesByCategory.value =
//                    filteredRecipes.groupBy { it.strCategory ?: "Uncategorized" }
//            }
//
//            _isLoading.value = false
//        }
//
//        override fun loadAllRecipes() {
//            _isLoading.value = true
//            _recipes.value = testRecipes
//            _recipesByCategory.value = testRecipes.groupBy { it.strCategory ?: "Uncategorized" }
//            _isLoading.value = false
//        }
//    }
//
//    // Simple profile view model that returns a test user profile
//    class SimpleProfileViewModel : IProfileViewModel {
//        private val testUser = User(
//            id = "123",
//            username = "testuser",
//            name = "Test User",
//            age = 30,
//            height = 180,
//            weight = 75,
//            fitness_goal = "weight loss",
//            nutrition_goals = mapOf(
//                "calories" to 2000,
//                "protein" to 150,
//                "carbs" to 200,
//                "fat" to 60,
//                "water" to 3
//            ),
//            password = "password"
//        )
//        private val _profile = MutableLiveData<Result<User>>()
//
//        override val profile: LiveData<Result<User>> = _profile
//
//        private val _updateResult = MutableLiveData<Result<User>?>(null)
//        override val updateResult: MutableLiveData<Result<User>?> = _updateResult
//
//        override fun fetchByUsername(username: String): Job {
//            _profile.postValue(Result.Success(testUser))
//            // Already set in constructor
//            return kotlinx.coroutines.CompletableDeferred<Unit>().also { it.complete(Unit) }
//        }
//
//        override fun updateProfile(username: String, req: UpdateProfileRequest): Job {
//
//            // Do nothing for test
//            return kotlinx.coroutines.CompletableDeferred<Unit>().also { it.complete(Unit) }
//        }
//
//        override fun clearUpdateState() {
//            _updateResult.value = null
//        }
//
//
//    }
//
//    private lateinit var navController: TestNavHostController
//    private lateinit var authViewModel: IAuthViewModel
//    private lateinit var homeViewModel: IHomeViewModel
//    private lateinit var profileViewModel: IProfileViewModel
//    private lateinit var recipeViewModel: IRecipeViewModel
//
//    @Composable
//    @Before
//    fun setUp() {
//        composeTestRule.runOnUiThread {
//            val context = ApplicationProvider.getApplicationContext<Context>()
//
//            navController = TestNavHostController(context)
//            navController.navigatorProvider.addNavigator(ComposeNavigator())
//            navController.graph = navController.createGraph(startDestination = "login") {
//                composable("login") { /* Test doesn't need content */ }
//                composable("register") { /* Test doesn't need content */ }
//                composable("home") { /* Test doesn't need content */ }
//            }
//
//            // Initialize simple view models
//            authViewModel = SimpleAuthViewModel()
//            homeViewModel = SimpleHomeViewModel()
//            profileViewModel = SimpleProfileViewModel()
//            recipeViewModel = FakeRecipeViewModel()
//        }
//        val context = LocalContext.current
//    }
//
//    @Test
//    fun testInitialNavigationToLoginScreen() {
//        ViewModelFactory.authViewModelForTesting = SimpleAuthViewModel()
//        // not logged in should start at login
//        composeTestRule.setContent {
//            LoginScreen(navController = navController, authViewModel = authViewModel)
//        }
//
//        // verify on login screen
//        composeTestRule.onNodeWithText("NOMSY").assertExists()
//        composeTestRule.onNodeWithText("Sign In").assertExists()
//
//
//    }
//
//    @Test
//    fun testNavToHome() {
//        ViewModelFactory.authViewModelForTesting = authViewModel
//        authViewModel.isLoggedIn
//
//        composeTestRule.setContent {
//            TestableNomsyAppNavHost(
//                authViewModel = authViewModel,
//                profileViewModel = profileViewModel,
//                homeViewModel = homeViewModel,
//                recipeViewModel = recipeViewModel
//            )
//        }
//
//
//        // wait for initial navigation
//        composeTestRule.waitForIdle()
//    }
//
//    @Test
//    fun testBottomNavigation_whenLoggedIn() {
//
//
//        // verify bottom nav is displayed
//        composeTestRule.onNodeWithContentDescription("Statistics").assertExists()
//        composeTestRule.onNodeWithContentDescription("Home").assertExists()
//        composeTestRule.onNodeWithContentDescription("Recipes").assertExists()
//        composeTestRule.onNodeWithContentDescription("Profile").assertExists()
//
//        // navigate to different tabs
//        composeTestRule.onNodeWithContentDescription("Statistics").performClick()
//        composeTestRule.waitForIdle()
//        // verify we're on statistics screen (add specific assertions)
//
//        composeTestRule.onNodeWithContentDescription("Recipes").performClick()
//        composeTestRule.waitForIdle()
//        // verify we're on recipes screen (add specific assertions)
//
//        composeTestRule.onNodeWithContentDescription("Profile").performClick()
//        composeTestRule.waitForIdle()
//        // verify we're on profile screen (add specific assertions)
//    }
//
////    @Test
////    fun testLogout_navigatesToLogin() {
////        // start logged in
////        ViewModelFactory.authViewModelForTesting = fakeAuthViewModel
////        fakeAuthViewModel.setLoggedIn(true)
////
////        composeTestRule.setContent {
////            NomsyAppNavHost()
////        }
////
////        composeTestRule.waitForIdle()
////
////        // verify on home screen
////        composeTestRule.onNodeWithContentDescription("Home").assertExists()
////
////        // trigger logout
////        fakeAuthViewModel.setLoggedIn(false)
////
////        composeTestRule.waitForIdle()
////
////        // verify redirected to login
////        composeTestRule.onNodeWithText("NOMSY").assertExists()
////        composeTestRule.onNodeWithText("Sign In").assertExists()
////    }
//}
//
//// fake classes for testing
//class FakeAuthViewModel : IAuthViewModel {
//    private val _isLoggedIn = MutableStateFlow(false)
//    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
//
//    private val _loginResult = MutableLiveData<Result<User>?>(null)
//    override val loginResult: LiveData<Result<User>?> = _loginResult
//
//    private val _registerResult = MutableLiveData<Result<User>>()
//    override val registerResult: LiveData<Result<User>> = _registerResult
//
//    private val _profileResult = MutableLiveData<Result<User>>()
//    override val profileResult: LiveData<Result<User>> = _profileResult
//
//    private var username = "testuser"
//    private var password = "password"
//    private var name = "Test User"
//    private var age = 30
//    private var height = 175
//    private var weight = 70
//    private var fitnessGoal = "Build Muscle"
//
//    // test control methods
//    fun setLoggedIn(loggedIn: Boolean) {
//        _isLoggedIn.value = loggedIn
//    }
//
//    fun setLoginSuccess() {
//        val user = User(
//            id = "1",
//            username = username,
//            password = password,
//            name = name,
//            age = age,
//            height = height,
//            weight = weight,
//            fitness_goal = fitnessGoal,
//            nutrition_goals = mapOf("calories" to 2000, "protein" to 150)
//        )
//        _loginResult.value = Result.Success(user)
//    }
//
//    fun setLoginError(message: String) {
//        _loginResult.value = Result.Error(Exception(message))
//    }
//
//    // implementation of interface methods
//    override fun login(username: String, password: String) {
//        this.username = username
//        this.password = password
//        // simulate login success by default
//        setLoginSuccess()
//    }
//
//    override fun getCurrentUsername(): String = username
//
//    override fun setCurrentUsername(username: String) {
//        this.username = username
//    }
//
//    override fun logout() {
//        setLoggedIn(false)
//    }
//
//    override fun register(user: User) {
//        _registerResult.value = Result.Success(user)
//    }
//
//    override fun fetchProfile(userId: String) {
//        // not needed for this test
//    }
//
//    override fun fetchProfileByUsername(username: String) {
//        // not needed for this test
//    }
//
//    override fun setCredentials(username: String, password: String, email: String) {
//        this.username = username
//        this.password = password
//    }
//
//    override fun setUserName(name: String) {
//        this.name = name
//    }
//
//    override fun setUserAge(age: Int) {
//        this.age = age
//    }
//
//    override fun setUserHeight(height: Int) {
//        this.height = height
//    }
//
//    override fun setUserWeight(weight: Int) {
//        this.weight = weight
//    }
//
//    override fun setUserFitnessGoal(goal: String) {
//        this.fitnessGoal = goal
//    }
//
//    override fun setUserNutritionGoals(goals: Map<String, Int>) {
//        // not needed for this test
//    }
//
//    override fun getUsername(): String = username
//
//    override fun getPassword(): String = password
//
//    override fun getUserName(): String = name
//
//    override fun getUserAge(): Int = age
//
//    override fun getUserHeight(): Int = height
//
//    override fun getUserWeight(): Int = weight
//
//    override fun getUserFitnessGoal(): String = fitnessGoal
//}
//
//class FakeProfileViewModel : IProfileViewModel {
//    private val testUser = User(
//        id = "123",
//        username = "testuser",
//        name = "Test User",
//        age = 30,
//        height = 180,
//        weight = 75,
//        fitness_goal = "weight loss",
//        nutrition_goals = mapOf(
//            "calories" to 2000,
//            "protein" to 150,
//            "carbs" to 200,
//            "fat" to 60,
//            "water" to 3
//        ),
//        password = "password"
//    )
//
//    private val _profile = MutableLiveData<Result<User>>(Result.Success(testUser))
//    override val profile: LiveData<Result<User>> = _profile
//
//    private val _updateResult = MutableLiveData<Result<User>?>(null)
//    override val updateResult: MutableLiveData<Result<User>?> = _updateResult
//
//    override fun fetchByUsername(username: String): Job {
//        // already set in constructor
//        return CompletableDeferred<Unit>().also { it.complete(Unit) }
//    }
//
//    override fun updateProfile(username: String, req: UpdateProfileRequest): Job {
//        // do nothing for test
//        return CompletableDeferred<Unit>().also { it.complete(Unit) }
//    }
//
//    override fun clearUpdateState() {
//        _updateResult.value = null
//    }
//}
//
//class FakeHomeViewModel : IHomeViewModel {
//    // nutrition data
//    private val _nutritionTotals = MutableLiveData<Result<DailySummaryEntity?>>(
//        Result.Success(
//            DailySummaryEntity(
//                date = "2025-04-14",
//                totalCalories = 1500,
//                totalProtein = 100,
//                totalCarbs = 150,
//                totalFat = 40,
//                waterLiters = 2.0
//            )
//        )
//    )
//    override val nutritionTotals: LiveData<Result<DailySummaryEntity?>> = _nutritionTotals
//
//    // meal data
//    private val _mealsByType = MutableLiveData<Result<Map<String, List<MealItem>>>>(
//        Result.Success(
//            mapOf(
//                "breakfast" to listOf(
//                    MealItem("Scrambled Eggs", 220, 2, 14, 16),
//                    MealItem("Whole Wheat Toast", 90, 15, 3, 1)
//                ),
//                "lunch" to listOf(
//                    MealItem("Grilled Chicken Salad", 350, 10, 32, 18)
//                ),
//                "dinner" to listOf(
//                    MealItem("Salmon", 300, 0, 25, 20),
//                    MealItem("Broccoli", 55, 10, 4, 0)
//                )
//            )
//        )
//    )
//
//    override val mealsByType: LiveData<Result<Map<String, List<MealItem>>>> = _mealsByType
//
//    // water intake
//    private val _waterIntake = MutableStateFlow(2.0)
//    override val waterIntake: StateFlow<Double> = _waterIntake.asStateFlow()
//
//    // date
//    override val selectedDate = MutableStateFlow(14)
//
//    // methods
//    override fun incrementDate() {
//        if (selectedDate.value < 14) selectedDate.value += 1
//    }
//
//    override fun decrementDate() {
//        if (selectedDate.value > 11) selectedDate.value -= 1
//    }
//
//    override fun updateWaterIntake(date: String, newWaterIntake: Double) {
//        _waterIntake.value = newWaterIntake
//    }
//
//    override fun deleteMeal(date: String, foodName: String) {
//        // do nothing for test
//    }
//
//    override fun refreshData() {
//        // do nothing for test
//    }
//}
//
//// factory to inject fake view models for testing
//object ViewModelFactory {
//    var authViewModelForTesting: IAuthViewModel? = null
//    var profileViewModelForTesting: IProfileViewModel? = null
//    var homeViewModelForTesting: IHomeViewModel? = null
//
//    fun resetForTesting() {
//        authViewModelForTesting = null
//        profileViewModelForTesting = null
//        homeViewModelForTesting = null
//    }
//}
//
//// extension of NomsyAppNavHost for testing
//@Composable
//fun TestNomsyAppNavHost() {
//    // use the real NomsyAppNavHost but with fake view models for testing
//    val context = LocalContext.current
//    val navController = rememberNavController()
//
//    // any test-specific setup can go here
//
//    NomsyAppNavHost(navController)
//}