package com.brelio.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.brelio.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        when (prefs[THEME_MODE_KEY]) {
            ThemeMode.Male.name -> ThemeMode.Male
            else -> ThemeMode.Female
        }
    }

    val hasOnboarded: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[HAS_ONBOARDED_KEY] ?: false
    }

    val languageCode: Flow<String?> = dataStore.data.map { prefs ->
        prefs[LANGUAGE_CODE_KEY]
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.name
        }
    }

    suspend fun setOnboarded() {
        dataStore.edit { prefs ->
            prefs[HAS_ONBOARDED_KEY] = true
        }
    }

    suspend fun setLanguage(code: String) {
        dataStore.edit { prefs ->
            prefs[LANGUAGE_CODE_KEY] = code
        }
    }

    private companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val HAS_ONBOARDED_KEY = booleanPreferencesKey("has_onboarded")
        val LANGUAGE_CODE_KEY = stringPreferencesKey("language_code")
    }
}
