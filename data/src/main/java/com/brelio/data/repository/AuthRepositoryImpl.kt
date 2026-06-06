package com.brelio.data.repository

import com.brelio.core.network.AuthTokenManager
import com.brelio.data.local.SessionManager
import com.brelio.data.remote.api.AuthApi
import com.brelio.data.remote.dto.ResetPasswordRequest
import com.brelio.data.remote.dto.SignInRequest
import com.brelio.data.remote.dto.SignUpRequest
import com.brelio.domain.model.Session
import com.brelio.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
    private val authTokenManager: AuthTokenManager,
) : AuthRepository {

    override val isAuthenticated: Flow<Boolean> = sessionManager.isAuthenticated

    override val currentSession: Flow<Session?> = sessionManager.currentSession

    override suspend fun signIn(email: String, password: String): Result<Session> {
        return runCatching {
            val response = authApi.signIn(body = SignInRequest(email, password))
            val session = Session(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                userId = response.user.id,
                email = response.user.email.orEmpty(),
            )
            sessionManager.saveSession(session)
            session
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Session> {
        return runCatching {
            val response = authApi.signUp(body = SignUpRequest(email, password))
            val session = Session(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                userId = response.user.id,
                email = response.user.email.orEmpty(),
            )
            sessionManager.saveSession(session)
            session
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return runCatching {
            authApi.resetPassword(body = ResetPasswordRequest(email))
        }
    }

    override suspend fun signOut() {
        val token = authTokenManager.getAccessTokenSync()
        if (token != null) {
            runCatching { authApi.signOut("Bearer $token") }
        }
        sessionManager.clearSession()
    }
}
