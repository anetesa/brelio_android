package com.brelio.feature.home

import androidx.lifecycle.viewModelScope
import com.brelio.core.mvi.MviViewModel
import com.brelio.domain.model.AppointmentStatus
import com.brelio.domain.model.DashboardStats
import com.brelio.domain.usecase.appointment.GetTodayAppointmentsUseCase
import com.brelio.domain.usecase.auth.ObserveSessionUseCase
import com.brelio.domain.usecase.salon.GetSalonContextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayAppointmentsUseCase: GetTodayAppointmentsUseCase,
    private val getSalonContextUseCase: GetSalonContextUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
) : MviViewModel<HomeState, HomeEvent, HomeEffect>(HomeState()) {

    init {
        loadDashboard()
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadDashboard()
            is HomeEvent.AppointmentClicked -> {
                sendEffect(HomeEffect.NavigateToAppointment(event.appointmentId))
            }
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            val session = observeSessionUseCase().filterNotNull().first()

            getSalonContextUseCase(session.userId)
                .onSuccess { context -> setState { copy(salonContext = context) } }

            getTodayAppointmentsUseCase(session.userId)
                .onSuccess { appointments ->
                    val stats = calculateStats(appointments)
                    setState {
                        copy(
                            isLoading = false,
                            appointments = appointments.sortedBy { it.startAt },
                            stats = stats,
                        )
                    }
                }
                .onFailure { throwable ->
                    setState {
                        copy(isLoading = false, error = throwable.message)
                    }
                }
        }
    }

    private fun calculateStats(
        appointments: List<com.brelio.domain.model.Appointment>,
    ): DashboardStats {
        val billable = appointments.filter {
            it.status == AppointmentStatus.Confirmed || it.status == AppointmentStatus.Completed
        }
        val revenue = billable.sumOf { appt ->
            appt.services.sumOf { it.price }
        }
        val noShows = appointments.count { it.status == AppointmentStatus.NoShow }
        val uniqueClients = appointments.map { it.clientId }.distinct().size

        return DashboardStats(
            todayAppointments = appointments.size,
            todayRevenue = revenue,
            totalClients = uniqueClients,
            noShowCount = noShows,
        )
    }
}
