package com.brelio.feature.onboarding

data class OnboardingState(
    val currentPage: Int = 0,
    val pageCount: Int = 3,
)

sealed interface OnboardingEvent {
    data class PageChanged(val page: Int) : OnboardingEvent
    data object NextClicked : OnboardingEvent
    data object SkipClicked : OnboardingEvent
    data object GetStartedClicked : OnboardingEvent
}

sealed interface OnboardingEffect {
    data object NavigateToSignIn : OnboardingEffect
}
