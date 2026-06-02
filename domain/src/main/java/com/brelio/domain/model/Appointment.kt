package com.brelio.domain.model

import java.time.LocalDateTime

data class Appointment(
    val id: String,
    val clientId: String,
    val clientName: String,
    val stylistId: String?,
    val stylistName: String?,
    val serviceName: String?,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val status: AppointmentStatus,
    val notes: String?,
    val paymentStatus: PaymentStatus,
    val paymentMethod: PaymentMethod?,
    val services: List<AppointmentService>,
)

data class AppointmentService(
    val id: String,
    val serviceName: String,
    val durationMin: Int,
    val price: Double,
)

enum class AppointmentStatus { Pending, Confirmed, Completed, Cancelled, NoShow }
enum class PaymentStatus { Unpaid, Paid }
enum class PaymentMethod { Cash, Card }
