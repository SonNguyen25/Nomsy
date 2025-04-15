//package com.example.nomsy.ui.navigation
//
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithContentDescription
//import androidx.navigation.compose.ComposeNavigator
//import androidx.navigation.testing.TestNavHostController
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//class NavigationTesti {
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//    lateinit var navController: TestNavHostController
//
//    @Before
//    fun setupAppNavHost() {
//        composeTestRule.setContent {
//            navController = TestNavHostController(LocalContext.current)
//            navController.navigatorProvider.addNavigator(ComposeNavigator())
//            NomsyAppNavHost(navController = navController)
//        }
//    }
//
//    // Unit test
//    @Test
//    fun appNavHost_verifyStartDestination() {
//        composeTestRule
//            .onNodeWithContentDescription("NOMSY")
//            .assertIsDisplayed()
//    }
//}
//
