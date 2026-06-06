package com.brelio.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SalonMemberDto(
    @SerialName("salon_id") val salonId: String,
    val role: String,
    val salons: SalonDto? = null,
)

@Serializable
data class SalonDto(
    @SerialName("user_id") val userId: String,
    val timezone: String? = null,
    val slug: String? = null,
    val country: String? = null,
)
