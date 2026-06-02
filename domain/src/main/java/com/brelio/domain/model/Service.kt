package com.brelio.domain.model

data class Service(
    val id: String,
    val name: String,
    val price: Double,
    val durationMin: Int,
    val description: String?,
)
