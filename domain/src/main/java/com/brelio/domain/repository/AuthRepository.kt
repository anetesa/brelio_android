package com.brelio.domain.repository

import com.brelio.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthenticated: Flow<Boolean>
    val currentSession: Flow<Session?>
    suspend fun signIn(email: String, password: String): Result<Session>
    suspend fun signInWithIdToken(provider: String, idToken: String): Result<Session>
    suspend fun signUp(email: String, password: String): Result<Session>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signOut()
}
