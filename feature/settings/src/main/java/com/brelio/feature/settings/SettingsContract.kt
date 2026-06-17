package com.brelio.feature.settings

import com.brelio.domain.model.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.Female,
    val languageCode: String = "en",
    val showSignOutDialog: Boolean = false,
)

sealed interface SettingsEvent {
    data class ThemeChanged(val mode: ThemeMode) : SettingsEvent
    data class LanguageChanged(val code: String) : SettingsEvent
    data object SignOutClicked : SettingsEvent
    data object ConfirmSignOut : SettingsEvent
    data object DismissDialog : SettingsEvent
}

sealed interface SettingsEffect {
    data object NavigateToSignIn : SettingsEffect
    data object RestartApp : SettingsEffect
}
