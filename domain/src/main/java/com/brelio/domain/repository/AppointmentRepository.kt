package com.brelio.domain.repository

import com.brelio.domain.model.Appointment
import java.time.LocalDate

interface AppointmentRepository {
    suspend fun getAppointments(userId: String, date: LocalDate): Result<List<Appointment>>
    suspend fun getAppointmentsByRange(
        userId: String,
        from: LocalDate,
        to: LocalDate,
    ): Result<List<Appointment>>
}
