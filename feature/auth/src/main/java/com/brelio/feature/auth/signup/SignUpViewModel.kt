package com.brelio.feature.auth.signup

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.brelio.core.designsystem.R
import com.brelio.core.mvi.MviViewModel
import com.brelio.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
) : MviViewModel<SignUpState, SignUpEvent, SignUpEffect>(SignUpState()) {

    override fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EmailChanged -> setState {
                copy(email = event.email, emailError = null)
            }

            is SignUpEvent.PasswordChanged -> setState {
                copy(password = event.password, passwordError = null)
            }

            is SignUpEvent.ConfirmPasswordChanged -> setState {
                copy(confirmPassword = event.confirmPassword, confirmPasswordError = null)
            }

            SignUpEvent.SignUpClicked -> handleSignUp()

            SignUpEvent.GoogleSignUpClicked -> sendEffect(SignUpEffect.NavigateToSignIn)
            SignUpEvent.SignInClicked -> sendEffect(SignUpEffect.NavigateToSignIn)
        }
    }

    private fun handleSignUp() {
        val state = currentState

        val emailError = when {
            state.email.isBlank() -> R.string.error_email_required
            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> R.string.error_invalid_email
            else -> null
        }
        val passwordError = when {
            state.password.isBlank() -> R.string.error_password_required
            state.password.length < 6 -> R.string.error_password_too_short
            else -> null
        }
        val confirmPasswordError = when {
            state.confirmPassword != state.password -> R.string.error_passwords_dont_match
            else -> null
        }

        if (emailError != null || passwordError != null || confirmPasswordError != null) {
            setState {
                copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                )
            }
            return
        }

        setState { copy(isLoading = true) }

        viewModelScope.launch {
            signUpUseCase(state.email, state.password)
                .onSuccess {
                    sendEffect(SignUpEffect.NavigateToHome)
                }
                .onFailure { exception ->
                    setState { copy(isLoading = false) }
                    sendEffect(
                        SignUpEffect.ShowError(
                            exception.localizedMessage.orEmpty()
                        )
                    )
                }
        }
    }
}
