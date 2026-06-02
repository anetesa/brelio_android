package com.brelio.domain.usecase.client

import com.brelio.domain.model.Client
import com.brelio.domain.repository.ClientRepository
import javax.inject.Inject

class GetClientsUseCase @Inject constructor(
    private val clientRepository: ClientRepository,
) {
    suspend operator fun invoke(ownerId: String): Result<List<Client>> {
        if (ownerId.isBlank()) return Result.failure(IllegalArgumentException("Owner ID required"))
        return clientRepository.getClients(ownerId)
    }
}
