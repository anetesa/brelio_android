package com.brelio.domain.model

data class Staff(
    val id: String,
    val name: String,
    val role: String,
    val photoUrl: String?,
    val isActive: Boolean,
)
