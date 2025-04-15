package com.example.nomsy.ui.screens.profile

import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.nomsy.ui.components.SectionCard
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SectionCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sectionCardDisplaysTitleAndContent() {
        val testTitle = "Profile Information"
        val testContent = "This is the content of the section."

        composeTestRule.setContent {
            SectionCard(title = testTitle) {
                Text(text = testContent, modifier = Modifier.testTag("contentText"))
            }
        }

        // Assert that the title and content texts appear.
        composeTestRule.onNodeWithText(testTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(testContent).assertIsDisplayed()
    }


    @Test
    fun sectionCardWithEmptyContentDisplaysTitleOnly() {
        val testTitle = "Empty Content Section"

        composeTestRule.setContent {
            SectionCard(title = testTitle) {
            }
        }

        composeTestRule.onNodeWithText(testTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText("Content").assertDoesNotExist()
    }


    @Test
    fun sectionCardContentIsBelowTitle() {
        val testTitle = "Section Title"
        val testContent = "Content goes here"

        composeTestRule.setContent {
            SectionCard(title = testTitle) {
                Text(text = testContent, modifier = Modifier.testTag("contentText"))
            }
        }

        val titleY = composeTestRule.onNodeWithText(testTitle)
            .fetchSemanticsNode().positionInRoot.y
        val contentY = composeTestRule.onNodeWithTag("contentText")
            .fetchSemanticsNode().positionInRoot.y

        assertTrue("Expected the title to be above the content", titleY < contentY)
    }
}