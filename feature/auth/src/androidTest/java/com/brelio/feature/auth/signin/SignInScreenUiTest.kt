package com.brelio.feature.auth.signin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.brelio.core.ui.BrelioButton
import com.brelio.core.ui.BrelioTextField
import org.junit.Rule
import org.junit.Test

// Tests sign-in form elements without requiring Hilt or ViewModel
class SignInScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emailFieldRendersWithLabel() {
        composeTestRule.setContent {
            BrelioTextField(
                value = "",
                onValueChange = {},
                label = "Email",
            )
        }
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    }

    @Test
    fun passwordFieldRendersWithToggle() {
        composeTestRule.setContent {
            BrelioTextField(
                value = "secret123",
                onValueChange = {},
                label = "Password",
                isPassword = true,
            )
        }
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Show password").assertIsDisplayed()
    }

    @Test
    fun signInButtonRendersEnabled() {
        composeTestRule.setContent {
            BrelioButton(
                text = "Sign In",
                onClick = {},
                enabled = true,
            )
        }
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }

    @Test
    fun signInButtonShowsSpinnerWhenLoading() {
        composeTestRule.setContent {
            BrelioButton(
                text = "Sign In",
                onClick = {},
                isLoading = true,
            )
        }
        composeTestRule.onNodeWithText("Sign In").assertDoesNotExist()
    }

    @Test
    fun emailErrorDisplaysValidationMessage() {
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
    fun passwordErrorDisplaysValidationMessage() {
        composeTestRule.setContent {
            BrelioTextField(
                value = "",
                onValueChange = {},
                label = "Password",
                isPassword = true,
                errorResId = com.brelio.core.designsystem.R.string.error_password_required,
            )
        }
        composeTestRule.onNodeWithText("Password is required").assertIsDisplayed()
    }
}
