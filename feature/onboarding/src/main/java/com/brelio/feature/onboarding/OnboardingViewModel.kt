package com.brelio.feature.onboarding

import androidx.lifecycle.viewModelScope
import com.brelio.core.mvi.MviViewModel
import com.brelio.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
) : MviViewModel<OnboardingState, OnboardingEvent, OnboardingEffect>(OnboardingState()) {

    override fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.PageChanged -> setState {
                copy(currentPage = event.page)
            }

            OnboardingEvent.NextClicked -> {
                val nextPage = currentState.currentPage + 1
                if (nextPage < currentState.pageCount) {
                    setState { copy(currentPage = nextPage) }
                } else {
                    completeOnboarding()
                }
            }

            OnboardingEvent.SkipClicked -> completeOnboarding()

            OnboardingEvent.GetStartedClicked -> completeOnboarding()
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setOnboarded(true)
            sendEffect(OnboardingEffect.NavigateToSignIn)
        }
    }
}
