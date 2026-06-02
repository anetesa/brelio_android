package com.brelio.domain.usecase.auth

import com.brelio.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email required"))
        return authRepository.resetPassword(email.trim())
    }
}
