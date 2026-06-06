package com.brelio.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.brelio.core.network.AuthTokenManager
import com.brelio.domain.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val authTokenManager: AuthTokenManager,
    private val dataStore: DataStore<Preferences>,
) {

    private val userId: Flow<String?> = dataStore.data.map { it[USER_ID_KEY] }
    private val userEmail: Flow<String?> = dataStore.data.map { it[USER_EMAIL_KEY] }

    val currentSession: Flow<Session?> = combine(
        authTokenManager.accessToken,
        authTokenManager.refreshToken,
        userId,
        userEmail,
    ) { accessToken, refreshToken, id, email ->
        if (accessToken != null && refreshToken != null && id != null) {
            Session(
                accessToken = accessToken,
                refreshToken = refreshToken,
                userId = id,
                email = email.orEmpty(),
            )
        } else {
            null
        }
    }

    val isAuthenticated: Flow<Boolean> = authTokenManager.accessToken.map { it != null }

    suspend fun saveSession(session: Session) {
        authTokenManager.saveTokens(session.accessToken, session.refreshToken)
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = session.userId
            prefs[USER_EMAIL_KEY] = session.email
        }
    }

    suspend fun clearSession() {
        authTokenManager.clearTokens()
        dataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
            prefs.remove(USER_EMAIL_KEY)
        }
    }

    suspend fun getAccessToken(): String? = authTokenManager.getAccessTokenSync()

    private companion object {
        val USER_ID_KEY = stringPreferencesKey("session_user_id")
        val USER_EMAIL_KEY = stringPreferencesKey("session_user_email")
    }
}
