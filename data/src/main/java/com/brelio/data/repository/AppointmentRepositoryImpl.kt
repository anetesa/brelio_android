package com.brelio.data.repository

import com.brelio.data.remote.api.AppointmentApi
import com.brelio.data.remote.dto.AppointmentDto
import com.brelio.domain.model.Appointment
import com.brelio.domain.model.AppointmentService
import com.brelio.domain.model.AppointmentStatus
import com.brelio.domain.model.PaymentMethod
import com.brelio.domain.model.PaymentStatus
import com.brelio.domain.repository.AppointmentRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepositoryImpl @Inject constructor(
    private val appointmentApi: AppointmentApi,
) : AppointmentRepository {

    override suspend fun getAppointments(
        userId: String,
        date: LocalDate,
    ): Result<List<Appointment>> {
        return runCatching {
            val startOfDay = date.atStartOfDay().format(ISO_FORMATTER)
            val endOfDay = date.plusDays(1).atStartOfDay().format(ISO_FORMATTER)
            appointmentApi.getAppointments(
                userId = "eq.$userId",
                startAtGte = "gte.$startOfDay",
                endAtLte = "lt.$endOfDay",
            ).map { it.toDomain() }
        }
    }

    override suspend fun getAppointmentsByRange(
        userId: String,
        from: LocalDate,
        to: LocalDate,
    ): Result<List<Appointment>> {
        return runCatching {
            val start = from.atStartOfDay().format(ISO_FORMATTER)
            val end = to.plusDays(1).atStartOfDay().format(ISO_FORMATTER)
            appointmentApi.getAppointments(
                userId = "eq.$userId",
                startAtGte = "gte.$start",
                endAtLte = "lt.$end",
            ).map { it.toDomain() }
        }
    }

    private fun AppointmentDto.toDomain(): Appointment {
        return Appointment(
            id = id,
            clientId = clientId,
            clientName = clients?.name.orEmpty(),
            stylistId = stylistId,
            stylistName = stylists?.name,
            serviceName = services.firstOrNull()?.serviceName,
            startAt = parseDateTime(startAt),
            endAt = parseDateTime(endAt),
            status = parseStatus(status),
            notes = notes,
            paymentStatus = parsePaymentStatus(paymentStatus),
            paymentMethod = parsePaymentMethod(paymentMethod),
            services = services.map { svc ->
                AppointmentService(
                    id = svc.id,
                    serviceName = svc.serviceName,
                    durationMin = svc.durationMin,
                    price = svc.price,
                )
            },
        )
    }

    private fun parseDateTime(raw: String): LocalDateTime {
        return runCatching {
            LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME)
        }.getOrDefault(LocalDateTime.MIN)
    }

    private fun parseStatus(raw: String): AppointmentStatus {
        return when (raw.lowercase()) {
            "confirmed" -> AppointmentStatus.Confirmed
            "completed" -> AppointmentStatus.Completed
            "cancelled" -> AppointmentStatus.Cancelled
            "no_show" -> AppointmentStatus.NoShow
            else -> AppointmentStatus.Pending
        }
    }

    private fun parsePaymentStatus(raw: String?): PaymentStatus {
        return when (raw?.lowercase()) {
            "paid" -> PaymentStatus.Paid
            else -> PaymentStatus.Unpaid
        }
    }

    private fun parsePaymentMethod(raw: String?): PaymentMethod? {
        return when (raw?.lowercase()) {
            "cash" -> PaymentMethod.Cash
            "card" -> PaymentMethod.Card
            else -> null
        }
    }

    private companion object {
        val ISO_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}
