package com.brelio.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientDto(
    val id: String,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val notes: String? = null,
    val birthday: String? = null,
    val tags: List<String>? = null,
    @SerialName("no_show_count") val noShowCount: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
)

@Serializable
data class CreateClientRequest(
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
)
