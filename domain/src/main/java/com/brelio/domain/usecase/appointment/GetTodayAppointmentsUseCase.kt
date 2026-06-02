package com.brelio.domain.usecase.appointment

import com.brelio.domain.model.Appointment
import com.brelio.domain.repository.AppointmentRepository
import java.time.LocalDate
import javax.inject.Inject

class GetTodayAppointmentsUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
) {
    suspend operator fun invoke(userId: String): Result<List<Appointment>> {
        if (userId.isBlank()) return Result.failure(IllegalArgumentException("User ID required"))
        return appointmentRepository.getAppointments(userId, LocalDate.now())
    }
}
