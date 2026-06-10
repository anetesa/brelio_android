package com.brelio.feature.auth.signin

import androidx.lifecycle.viewModelScope
import com.brelio.core.designsystem.R
import com.brelio.core.mvi.MviViewModel
import com.brelio.domain.usecase.auth.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
) : MviViewModel<SignInState, SignInEvent, SignInEffect>(SignInState()) {

    override fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.EmailChanged -> setState {
                copy(email = event.email, emailError = null)
            }

            is SignInEvent.PasswordChanged -> setState {
                copy(password = event.password, passwordError = null)
            }

            SignInEvent.SignInClicked -> handleSignIn()

            SignInEvent.GoogleSignInClicked -> handleGoogleSignIn()
            SignInEvent.ForgotPasswordClicked -> sendEffect(SignInEffect.NavigateToResetPassword)
            SignInEvent.SignUpClicked -> sendEffect(SignInEffect.NavigateToSignUp)
        }
    }

    private fun handleGoogleSignIn() {
        // Credential Manager integration will be added with google-services.json setup
    }

    private fun handleSignIn() {
        val state = currentState

        val emailError = when {
            state.email.isBlank() -> R.string.error_email_required
            else -> null
        }
        val passwordError = when {
            state.password.isBlank() -> R.string.error_password_required
            state.password.length < 6 -> R.string.error_password_too_short
            else -> null
        }

        if (emailError != null || passwordError != null) {
            setState { copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        setState { copy(isLoading = true) }

        viewModelScope.launch {
            signInUseCase(state.email, state.password)
                .onSuccess {
                    sendEffect(SignInEffect.NavigateToHome)
                }
                .onFailure { exception ->
                    setState { copy(isLoading = false) }
                    sendEffect(
                        SignInEffect.ShowError(
                            exception.message ?: "Authentication failed"
                        )
                    )
                }
        }
    }
}
