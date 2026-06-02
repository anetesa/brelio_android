package com.brelio.domain.model

data class SalonContext(
    val salonId: String,
    val ownerUserId: String,
    val timezone: String,
    val role: SalonRole,
    val salonSlug: String?,
    val country: String,
)

enum class SalonRole { Owner, Manager, Stylist }
