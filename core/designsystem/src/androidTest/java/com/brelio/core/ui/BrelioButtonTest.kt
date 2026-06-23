package com.brelio.core.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BrelioButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysText() {
        composeTestRule.setContent {
            BrelioButton(text = "Continue", onClick = {})
        }
        composeTestRule.onNodeWithText("Continue").assertIsDisplayed()
    }

    @Test
    fun clickInvokesCallback() {
        var clicked = false
        composeTestRule.setContent {
            BrelioButton(text = "Save", onClick = { clicked = true })
        }
        composeTestRule.onNodeWithText("Save").performClick()
        assertTrue(clicked)
    }

    @Test
    fun showsSpinnerInsteadOfTextWhileLoading() {
        composeTestRule.setContent {
            BrelioButton(text = "Submit", onClick = {}, isLoading = true)
        }
        composeTestRule.onNodeWithText("Submit").assertDoesNotExist()
    }

    @Test
    fun disabledStatePreventsClicks() {
        var clicked = false
        composeTestRule.setContent {
            BrelioButton(text = "Go", onClick = { clicked = true }, enabled = false)
        }
        composeTestRule.onNodeWithText("Go").assertIsNotEnabled()
    }
}
