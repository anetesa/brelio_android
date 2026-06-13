package com.brelio.feature.calendar

import com.brelio.domain.model.Appointment
import java.time.LocalDate

data class CalendarState(
    val selectedDate: LocalDate = LocalDate.now(),
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = true,
)

sealed interface CalendarEvent {
    data class DateSelected(val date: LocalDate) : CalendarEvent
    data object NextDay : CalendarEvent
    data object PreviousDay : CalendarEvent
    data class AppointmentClicked(val appointmentId: String) : CalendarEvent
}

sealed interface CalendarEffect {
    data class NavigateToAppointment(val appointmentId: String) : CalendarEffect
}
