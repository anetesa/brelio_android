package com.brelio.domain.usecase.auth

import com.brelio.domain.model.Session
import com.brelio.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<Session> {
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email required"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Password too short"))
        return authRepository.signIn(email.trim(), password)
    }
}
