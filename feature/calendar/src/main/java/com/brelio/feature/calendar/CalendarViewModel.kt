package com.brelio.feature.calendar

import androidx.lifecycle.viewModelScope
import com.brelio.core.mvi.MviViewModel
import com.brelio.domain.usecase.appointment.GetAppointmentsByDateUseCase
import com.brelio.domain.usecase.auth.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getAppointmentsByDateUseCase: GetAppointmentsByDateUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
) : MviViewModel<CalendarState, CalendarEvent, CalendarEffect>(CalendarState()) {

    init {
        loadAppointments(currentState.selectedDate)
    }

    override fun onEvent(event: CalendarEvent) {
        when (event) {
            is CalendarEvent.DateSelected -> navigateToDate(event.date)
            is CalendarEvent.NextDay -> navigateToDate(currentState.selectedDate.plusDays(1))
            is CalendarEvent.PreviousDay -> navigateToDate(currentState.selectedDate.minusDays(1))
            is CalendarEvent.AppointmentClicked ->
                sendEffect(CalendarEffect.NavigateToAppointment(event.appointmentId))
        }
    }

    private fun navigateToDate(date: LocalDate) {
        if (date == currentState.selectedDate && !currentState.isLoading) return
        setState { copy(selectedDate = date) }
        loadAppointments(date)
    }

    private fun loadAppointments(date: LocalDate) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val session = observeSessionUseCase().filterNotNull().first()

            getAppointmentsByDateUseCase(session.userId, date)
                .onSuccess { appointments ->
                    setState {
                        copy(
                            isLoading = false,
                            appointments = appointments.sortedBy { it.startAt },
                        )
                    }
                }
                .onFailure {
                    setState { copy(isLoading = false) }
                }
        }
    }
}
