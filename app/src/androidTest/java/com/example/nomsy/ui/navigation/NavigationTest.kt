//package com.example.nomsy.ui.navigation
//
//import android.content.Context
//import androidx.compose.material.Scaffold
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.navigation.compose.ComposeNavigator
//import androidx.navigation.compose.composable
//import androidx.navigation.createGraph
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.core.app.ApplicationProvider
//import com.example.nomsy.ui.navigation.BottomNavItem.Statistics
//import com.example.nomsy.ui.navigation.BottomNavItem.Home
//import com.example.nomsy.ui.navigation.BottomNavItem.Recipes
//import com.example.nomsy.ui.navigation.BottomNavItem.Profile
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
///**
// * This test creates a simplified navigation graph with four paths (Statistics, Home, Recipes, and Profile).
// * Starting from the Home screen, it presses each bottom navigation option and asserts that the expected screen is displayed.
// */
//class BottomNavigationTest {
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    private lateinit var navController: TestNavHostController
//
//    /**
//     * Setup the test NavHostController and a simplified navigation graph.
//     *
//     * Here we create a test graph with four composable destinations.
//     * We then set the content to the NomsyAppNavHost, which includes a bottom bar that shows the bottom navigation items.
//     */
//    @Before
//    fun setup() {
//        composeTestRule.runOnUiThread {
//            val context: Context = ApplicationProvider.getApplicationContext()
//            navController = TestNavHostController(context).apply {
//                navigatorProvider.addNavigator(ComposeNavigator())
//                graph = createGraph(startDestination = Home.route) {
//                    composable(Statistics.route) { TestScreen("Statistics Screen") }
//                    composable(Home.route) { TestScreen("Home Screen") }
//                    composable(Recipes.route) { TestScreen("Recipes Screen") }
//                    composable(Profile.route) { TestScreen("Profile Screen") }
//                }
//            }
//        }
//
//        // Set the content to our NomsyAppNavHost which renders the bottom bar when the current route is one of the four.
//        composeTestRule.setContent {
//            NomsyAppNavHost(navController = navController)
//        }
//    }
//
//    /**
//     * A simple composable used for testing.
//     */
//    @Composable
//    fun TestScreen(screenText: String) {
//        // In a real app, this would be your full screen UI.
//        Text(text = screenText)
//    }
//
//    /**
//     * This test simulates clicking on each bottom navigation item.
//     * It checks that after clicking, the expected screen (displaying the respective text) shows up.
//     */
//    @Test
//    fun testBottomNavigation() {
//        // Start at Home: make sure Home Screen is displayed initially
//        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
//        assertEquals(Home.route, navController.currentBackStackEntry?.destination?.route)
//
//        // Navigate to Statistics Screen by clicking the Statistics bottom bar item.
//        composeTestRule.onNodeWithText("Statistics").performClick()
//        composeTestRule.waitForIdle()
//        composeTestRule.onNodeWithText("Statistics Screen").assertIsDisplayed()
//        assertEquals(Statistics.route, navController.currentBackStackEntry?.destination?.route)
//
//        // Navigate to Recipes Screen
//        composeTestRule.onNodeWithText("Recipes").performClick()
//        composeTestRule.waitForIdle()
//        composeTestRule.onNodeWithText("Recipes Screen").assertIsDisplayed()
//        assertEquals(Recipes.route, navController.currentBackStackEntry?.destination?.route)
//
//        // Navigate to Profile Screen
//        composeTestRule.onNodeWithText("Profile").performClick()
//        composeTestRule.waitForIdle()
//        composeTestRule.onNodeWithText("Profile Screen").assertIsDisplayed()
//        assertEquals(Profile.route, navController.currentBackStackEntry?.destination?.route)
//
//        // Finally, navigate back to Home Screen
//        composeTestRule.onNodeWithText("Home").performClick()
//        composeTestRule.waitForIdle()
//        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
//        assertEquals(Home.route, navController.currentBackStackEntry?.destination?.route)
//    }
//}
//
//
