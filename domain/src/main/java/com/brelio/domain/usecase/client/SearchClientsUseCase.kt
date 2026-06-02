package com.brelio.domain.usecase.client

import com.brelio.domain.model.Client
import com.brelio.domain.repository.ClientRepository
import javax.inject.Inject

class SearchClientsUseCase @Inject constructor(
    private val clientRepository: ClientRepository,
) {
    suspend operator fun invoke(ownerId: String, query: String): Result<List<Client>> {
        if (ownerId.isBlank()) return Result.failure(IllegalArgumentException("Owner ID required"))
        if (query.isBlank()) return clientRepository.getClients(ownerId)
        return clientRepository.searchClients(ownerId, query.trim())
    }
}
