package com.brelio.feature.auth.signup

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
)

sealed interface SignUpEvent {
    data class EmailChanged(val email: String) : SignUpEvent
    data class PasswordChanged(val password: String) : SignUpEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignUpEvent
    data object SignUpClicked : SignUpEvent
    data object GoogleSignUpClicked : SignUpEvent
    data object SignInClicked : SignUpEvent
}

sealed interface SignUpEffect {
    data object NavigateToHome : SignUpEffect
    data object NavigateToSignIn : SignUpEffect
    data class ShowError(val message: String) : SignUpEffect
}
