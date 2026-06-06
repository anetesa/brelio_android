package com.brelio.data.repository

import com.brelio.data.remote.api.ClientApi
import com.brelio.data.remote.dto.ClientDto
import com.brelio.data.remote.dto.CreateClientRequest
import com.brelio.domain.model.Client
import com.brelio.domain.repository.ClientRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepositoryImpl @Inject constructor(
    private val clientApi: ClientApi,
) : ClientRepository {

    override suspend fun getClients(ownerId: String): Result<List<Client>> {
        return runCatching {
            clientApi.getClients(ownerId = "eq.$ownerId").map { it.toDomain() }
        }
    }

    override suspend fun searchClients(ownerId: String, query: String): Result<List<Client>> {
        return runCatching {
            clientApi.searchClients(
                ownerId = "eq.$ownerId",
                name = "ilike.*${query}*",
            ).map { it.toDomain() }
        }
    }

    override suspend fun getClient(clientId: String): Result<Client> {
        return runCatching {
            clientApi.getClient(id = "eq.$clientId").first().toDomain()
        }
    }

    override suspend fun createClient(
        ownerId: String,
        name: String,
        phone: String?,
        email: String?,
    ): Result<Client> {
        return runCatching {
            clientApi.createClient(
                body = CreateClientRequest(
                    ownerId = ownerId,
                    name = name,
                    phone = phone,
                    email = email,
                )
            ).first().toDomain()
        }
    }

    override suspend fun updateClient(client: Client): Result<Client> {
        return runCatching {
            clientApi.updateClient(
                id = "eq.${client.id}",
                body = ClientDto(
                    id = client.id,
                    name = client.name,
                    phone = client.phone,
                    email = client.email,
                    avatarUrl = client.avatarUrl,
                    notes = client.notes,
                    birthday = client.birthday,
                    tags = client.tags.ifEmpty { null },
                    noShowCount = client.noShowCount,
                    createdAt = client.createdAt,
                ),
            ).first().toDomain()
        }
    }

    override suspend fun deleteClient(clientId: String): Result<Unit> {
        return runCatching {
            clientApi.deleteClient(id = "eq.$clientId")
        }
    }

    private fun ClientDto.toDomain(): Client {
        return Client(
            id = id,
            name = name,
            phone = phone,
            email = email,
            avatarUrl = avatarUrl,
            notes = notes,
            birthday = birthday,
            tags = tags.orEmpty(),
            noShowCount = noShowCount ?: 0,
            createdAt = createdAt.orEmpty(),
        )
    }
}
