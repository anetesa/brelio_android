package com.brelio.feature.auth.reset

data class ResetPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: Int? = null,
    val isSuccess: Boolean = false,
)

sealed interface ResetPasswordEvent {
    data class EmailChanged(val email: String) : ResetPasswordEvent
    data object ResetClicked : ResetPasswordEvent
    data object BackClicked : ResetPasswordEvent
}

sealed interface ResetPasswordEffect {
    data object NavigateBack : ResetPasswordEffect
    data class ShowError(val message: String) : ResetPasswordEffect
}
