package com.brelio.feature.auth.reset

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.brelio.core.designsystem.R
import com.brelio.core.mvi.MviViewModel
import com.brelio.domain.usecase.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : MviViewModel<ResetPasswordState, ResetPasswordEvent, ResetPasswordEffect>(ResetPasswordState()) {

    override fun onEvent(event: ResetPasswordEvent) {
        when (event) {
            is ResetPasswordEvent.EmailChanged -> setState {
                copy(email = event.email, emailError = null, isSuccess = false)
            }

            ResetPasswordEvent.ResetClicked -> handleReset()

            ResetPasswordEvent.BackClicked -> {
                sendEffect(ResetPasswordEffect.NavigateBack)
            }
        }
    }

    private fun handleReset() {
        val state = currentState

        val emailError = when {
            state.email.isBlank() -> R.string.error_email_required
            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> R.string.error_invalid_email
            else -> null
        }

        if (emailError != null) {
            setState { copy(emailError = emailError) }
            return
        }

        setState { copy(isLoading = true) }

        viewModelScope.launch {
            resetPasswordUseCase(state.email)
                .onSuccess {
                    setState { copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { exception ->
                    setState { copy(isLoading = false) }
                    sendEffect(
                        ResetPasswordEffect.ShowError(
                            exception.message ?: "Password reset failed"
                        )
                    )
                }
        }
    }
}
