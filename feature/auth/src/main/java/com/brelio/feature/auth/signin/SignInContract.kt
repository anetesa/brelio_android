package com.brelio.feature.auth.signin

data class SignInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: Int? = null,
    val passwordError: Int? = null,
)

sealed interface SignInEvent {
    data class EmailChanged(val email: String) : SignInEvent
    data class PasswordChanged(val password: String) : SignInEvent
    data object SignInClicked : SignInEvent
    data object GoogleSignInClicked : SignInEvent
    data object ForgotPasswordClicked : SignInEvent
    data object SignUpClicked : SignInEvent
}

sealed interface SignInEffect {
    data object NavigateToHome : SignInEffect
    data object NavigateToSignUp : SignInEffect
    data object NavigateToResetPassword : SignInEffect
    data class ShowError(val message: String) : SignInEffect
}
