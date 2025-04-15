//package com.example.nomsy.ui.screens.recipe
//
//import android.content.Context
//import androidx.compose.ui.test.*
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.navigation.compose.ComposeNavigator
//import androidx.navigation.compose.composable
//import androidx.navigation.createGraph
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.example.nomsy.viewModels.RecipeViewModel
//import com.example.nomsy.ui.screens.recipes.recipesScreen
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class RecipeScreenTest {
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    private lateinit var navController: TestNavHostController
//    private lateinit var viewModel: RecipeViewModel
//    private val testDispatcher = StandardTestDispatcher()
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Before
//    fun setup() {
//        composeTestRule.runOnUiThread {
//            viewModel = RecipeViewModel(FakeRecipeRepository())
//
//            navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//            navController.navigatorProvider.addNavigator(ComposeNavigator())
//
//            val graph = navController.createGraph(startDestination = "recipes") {
//                composable("recipes") {
//                    recipesScreen(
//                        navController = navController,
//                        viewModel = viewModel
//                    )
//                }
//            }
//            navController.setGraph(graph)
//        }
//
//        composeTestRule.setContent {
//            recipesScreen(
//                navController = navController,
//                viewModel = viewModel
//            )
//        }
//    }
//
//
//
//    @Test
//    fun displaysTitleAndRecipeCardAndOpensPopup() {
//        composeTestRule.mainClock.advanceTimeByFrame()
//        composeTestRule.waitUntil(timeoutMillis = 5_000) {
//            viewModel.recipes.value.isNotEmpty()
//        }
//        composeTestRule.waitForIdle()
//
//        composeTestRule.onNodeWithTag("CookBookTitle").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("Category_Seafood").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("RecipeCard_1").assertIsDisplayed()
//    }
//}
