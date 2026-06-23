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

    val isLastPage get() = currentState.currentPage == currentState.pageCount - 1

    override fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.PageChanged -> setState { copy(currentPage = event.page) }
            OnboardingEvent.NextClicked -> if (isLastPage) finish() else advancePage()
            OnboardingEvent.SkipClicked,
            OnboardingEvent.GetStartedClicked -> finish()
        }
    }

    private fun advancePage() = setState { copy(currentPage = currentPage + 1) }

    private fun finish() {
        viewModelScope.launch {
            preferencesManager.setOnboarded()
            sendEffect(OnboardingEffect.NavigateToSignIn)
        }
    }
}
