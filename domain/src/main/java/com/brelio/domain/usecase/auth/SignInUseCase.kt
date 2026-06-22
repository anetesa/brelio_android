package com.brelio.domain.usecase.auth

import com.brelio.domain.model.Session
import com.brelio.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<Session> {
        require(email.isNotBlank()) { "Email required" }
        require(password.length >= MIN_PASSWORD_LENGTH) { "Password too short" }
        return authRepository.signIn(email.trim(), password)
    }
}

private const val MIN_PASSWORD_LENGTH = 6
