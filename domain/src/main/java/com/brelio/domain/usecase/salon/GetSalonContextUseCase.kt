package com.brelio.domain.usecase.salon

import com.brelio.domain.model.SalonContext
import com.brelio.domain.repository.SalonRepository
import javax.inject.Inject

class GetSalonContextUseCase @Inject constructor(
    private val salonRepository: SalonRepository,
) {
    suspend operator fun invoke(userId: String): Result<SalonContext?> {
        if (userId.isBlank()) return Result.failure(IllegalArgumentException("User ID required"))
        return salonRepository.getSalonContext(userId)
    }
}
