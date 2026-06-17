package com.brelio.feature.settings

import androidx.lifecycle.viewModelScope
import com.brelio.core.mvi.MviViewModel
import com.brelio.data.local.PreferencesManager
import com.brelio.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val signOutUseCase: SignOutUseCase,
) : MviViewModel<SettingsState, SettingsEvent, SettingsEffect>(SettingsState()) {

    init {
        observePreferences()
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ThemeChanged -> {
                viewModelScope.launch {
                    preferencesManager.setThemeMode(event.mode)
                }
            }
            is SettingsEvent.LanguageChanged -> {
                setState { copy(languageCode = event.code) }
                sendEffect(SettingsEffect.RestartApp)
            }
            is SettingsEvent.SignOutClicked -> {
                setState { copy(showSignOutDialog = true) }
            }
            is SettingsEvent.ConfirmSignOut -> {
                setState { copy(showSignOutDialog = false) }
                viewModelScope.launch {
                    signOutUseCase()
                    sendEffect(SettingsEffect.NavigateToSignIn)
                }
            }
            is SettingsEvent.DismissDialog -> {
                setState { copy(showSignOutDialog = false) }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesManager.themeMode.collect { mode ->
                setState { copy(themeMode = mode) }
            }
        }
    }
}
