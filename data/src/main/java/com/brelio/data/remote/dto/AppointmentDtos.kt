package com.brelio.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentDto(
    val id: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("stylist_id") val stylistId: String? = null,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    val status: String,
    val notes: String? = null,
    @SerialName("payment_status") val paymentStatus: String? = null,
    @SerialName("payment_method") val paymentMethod: String? = null,
    val clients: ClientRefDto? = null,
    val stylists: StylistRefDto? = null,
    @SerialName("appointment_services") val services: List<AppointmentServiceDto> = emptyList(),
)

@Serializable
data class ClientRefDto(val name: String)

@Serializable
data class StylistRefDto(val name: String)

@Serializable
data class AppointmentServiceDto(
    val id: String,
    @SerialName("service_name") val serviceName: String,
    @SerialName("duration_min") val durationMin: Int,
    val price: Double,
)
