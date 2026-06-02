package com.brelio.domain.usecase.auth

import com.brelio.domain.model.Session
import com.brelio.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<Session?> = authRepository.currentSession
}
