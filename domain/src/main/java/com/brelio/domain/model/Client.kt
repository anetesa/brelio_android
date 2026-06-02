package com.brelio.domain.model

data class Client(
    val id: String,
    val name: String,
    val phone: String?,
    val email: String?,
    val avatarUrl: String?,
    val notes: String?,
    val birthday: String?,
    val tags: List<String>,
    val noShowCount: Int,
    val createdAt: String,
)
