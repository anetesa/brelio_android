package com.brelio.feature.auth.signin

import androidx.lifecycle.viewModelScope
import com.brelio.core.designsystem.R
import com.brelio.core.mvi.MviViewModel
import com.brelio.domain.repository.AuthRepository
import com.brelio.domain.usecase.auth.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val authRepository: AuthRepository,
    @Named("google_web_client_id") private val googleWebClientId: String,
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
            SignInEvent.GoogleSignInClicked -> sendEffect(SignInEffect.LaunchGoogleSignIn(googleWebClientId))
            is SignInEvent.GoogleIdTokenReceived -> handleGoogleToken(event.idToken)
            SignInEvent.ForgotPasswordClicked -> sendEffect(SignInEffect.NavigateToResetPassword)
            SignInEvent.SignUpClicked -> sendEffect(SignInEffect.NavigateToSignUp)
        }
    }

    private fun handleGoogleToken(idToken: String) {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.signInWithIdToken("google", idToken)
                .onSuccess { sendEffect(SignInEffect.NavigateToHome) }
                .onFailure { e ->
                    setState { copy(isLoading = false) }
                    sendEffect(SignInEffect.ShowError(e.message ?: "Google sign-in failed"))
                }
        }
    }

    private fun handleSignIn() {
        val state = currentState
        val emailError = if (state.email.isBlank()) R.string.error_email_required else null
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
                .onSuccess { sendEffect(SignInEffect.NavigateToHome) }
                .onFailure { e ->
                    setState { copy(isLoading = false) }
                    sendEffect(SignInEffect.ShowError(e.message ?: "Authentication failed"))
                }
        }
    }
}
