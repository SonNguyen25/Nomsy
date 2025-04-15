package com.example.nomsy.ui.screens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nomsy.data.local.entities.User
import com.example.nomsy.ui.components.ProfileContent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockUser: User

    @Before
    fun setup() {
        mockUser = User(
            id = "123",
            name = "John Doe",
            age = 28,
            height = 175,
            weight = 70,
            nutrition_goals = mapOf(
                "water" to 2,
                "calories" to 2000,
                "protein" to 150,
                "carbs" to 200,
                "fat" to 70
            ),
            username = "joee",
            password = "TODO",
            fitness_goal = "maintain"
        )
        composeTestRule.setContent {
            ProfileContent(user = mockUser)
        }
    }

    @Test
    fun assertUserBasicInfo() {
        composeTestRule.onNodeWithText("John Doe")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Age: 28")
            .assertIsDisplayed()
    }

    @Test
    fun assertMeasurementsSection() {
        composeTestRule.onNodeWithText("Measurements")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Height")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("175 cm")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Weight")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("70 kg")
            .assertIsDisplayed()
    }

    @Test
    fun assertNutritionGoalsSection() {
        composeTestRule.onNodeWithText("Nutrition Goals")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Water")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("2 L")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Calories")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("2000 kcal")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Protein")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("150 g")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Carbs")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("200 g")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Fat")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("70 g")
            .assertIsDisplayed()
    }
}