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
            startAt = startAt.toLocalDateTimeOrMin(),
            endAt = endAt.toLocalDateTimeOrMin(),
            status = status.toAppointmentStatus(),
            notes = notes,
            paymentStatus = paymentStatus.toPaymentStatus(),
            paymentMethod = paymentMethod.toPaymentMethod(),
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

    private fun String.toLocalDateTimeOrMin(): LocalDateTime =
        runCatching { LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME) }
            .getOrDefault(LocalDateTime.MIN)

    private fun String.toAppointmentStatus() = when (lowercase()) {
        "confirmed" -> AppointmentStatus.Confirmed
        "completed" -> AppointmentStatus.Completed
        "cancelled" -> AppointmentStatus.Cancelled
        "no_show" -> AppointmentStatus.NoShow
        else -> AppointmentStatus.Pending
    }

    private fun String?.toPaymentStatus() =
        if (this == "paid") PaymentStatus.Paid else PaymentStatus.Unpaid

    private fun String?.toPaymentMethod() = when (this) {
        "cash" -> PaymentMethod.Cash
        "card" -> PaymentMethod.Card
        else -> null
    }

    private companion object {
        val ISO_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}
