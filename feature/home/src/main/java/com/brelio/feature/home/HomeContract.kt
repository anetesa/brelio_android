package com.brelio.feature.home

import com.brelio.domain.model.Appointment
import com.brelio.domain.model.DashboardStats
import com.brelio.domain.model.SalonContext

data class HomeState(
    val isLoading: Boolean = true,
    val appointments: List<Appointment> = emptyList(),
    val stats: DashboardStats = DashboardStats(0, 0.0, 0, 0),
    val salonContext: SalonContext? = null,
    val error: String? = null,
)

sealed interface HomeEvent {
    data object Refresh : HomeEvent
    data class AppointmentClicked(val appointmentId: String) : HomeEvent
}

sealed interface HomeEffect {
    data class NavigateToAppointment(val appointmentId: String) : HomeEffect
}
