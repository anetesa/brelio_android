package com.brelio.domain.repository

import com.brelio.domain.model.Client

interface ClientRepository {
    suspend fun getClients(ownerId: String): Result<List<Client>>
    suspend fun searchClients(ownerId: String, query: String): Result<List<Client>>
    suspend fun getClient(clientId: String): Result<Client>
    suspend fun createClient(
        ownerId: String,
        name: String,
        phone: String?,
        email: String?,
    ): Result<Client>
    suspend fun updateClient(client: Client): Result<Client>
    suspend fun deleteClient(clientId: String): Result<Unit>
}
