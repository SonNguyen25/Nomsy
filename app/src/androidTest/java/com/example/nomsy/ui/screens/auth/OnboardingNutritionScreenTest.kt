package com.example.nomsy.ui.screens.auth

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.viewModels.AuthViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingNutritionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        composeTestRule.runOnUiThread {
            val context: Context = ApplicationProvider.getApplicationContext()
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            authViewModel = AuthViewModel(context as Application).apply {
                setUserAge(25)
                setUserWeight(70)
                setUserHeight(175)
                setUserFitnessGoal("maintain")
            }

            navController.graph = navController.createGraph(startDestination = "onboarding_nutrition") {
                composable("onboarding_nutrition") { OnboardingNutritionScreen(navController, authViewModel) }
                composable("registration_complete") { /* Registration Complete Screen stub */ }
            }
        }

        composeTestRule.setContent {
            OnboardingNutritionScreen(navController, authViewModel)
        }
    }

    @Test
    fun testInitialState() {
        composeTestRule.onNodeWithText("What are your nutrition goals?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calories:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Protein:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Carbs:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fat:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Water:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calculate for me").assertIsDisplayed()
    }

    @Test
    fun testCalculateButton() {
        composeTestRule.onNodeWithText("Calculate for me").performClick()
        val allInputs = composeTestRule.onAllNodes(hasSetTextAction())

        allInputs.onFirst().assertTextContains("1673")
        allInputs[1].assertTextContains("126")
        allInputs[2].assertTextContains("167")
        allInputs[3].assertTextContains("55")
        allInputs[4].assertTextContains("2")
    }

    @Test
    fun testEmptyFieldsValidation() {
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals(initialRoute, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun testInvalidInputValidation() {
        composeTestRule.onAllNodes(hasSetTextAction()).onFirst().performTextInput("abc")

        composeTestRule.onNodeWithText("Next").performClick()
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals("onboarding_nutrition", currentRoute)
    }

    @Test
    fun testValidInputNavigation() {
        composeTestRule.onNodeWithText("Calculate for me").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        assertEquals("registration_complete", navController.currentBackStackEntry?.destination?.route)
    }
}
