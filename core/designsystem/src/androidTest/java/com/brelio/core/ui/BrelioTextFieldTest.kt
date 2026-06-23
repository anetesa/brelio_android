package com.brelio.core.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class BrelioTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsLabel() {
        composeTestRule.setContent {
            BrelioTextField(
                value = "",
                onValueChange = {},
                label = "Email address",
            )
        }
        composeTestRule.onNodeWithText("Email address").assertIsDisplayed()
    }

    @Test
    fun displaysErrorWhenResourceProvided() {
        composeTestRule.setContent {
            BrelioTextField(
                value = "",
                onValueChange = {},
                label = "Email",
                errorResId = com.brelio.core.designsystem.R.string.error_email_required,
            )
        }
        composeTestRule.onNodeWithText("Email is required").assertIsDisplayed()
    }

    @Test
    fun passwordFieldShowsVisibilityToggle() {
        composeTestRule.setContent {
            BrelioTextField(
                value = "secret",
                onValueChange = {},
                label = "Password",
                isPassword = true,
            )
        }
        composeTestRule.onNodeWithContentDescription("Show password").assertIsDisplayed()
    }

    @Test
    fun noToggleForRegularField() {
        composeTestRule.setContent {
            BrelioTextField(
                value = "hello",
                onValueChange = {},
                label = "Name",
            )
        }
        composeTestRule.onNodeWithContentDescription("Show password").assertDoesNotExist()
    }
}
