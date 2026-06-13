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
            is CalendarEvent.DateSelected -> {
                setState { copy(selectedDate = event.date) }
                loadAppointments(event.date)
            }
            is CalendarEvent.NextDay -> {
                val next = currentState.selectedDate.plusDays(1)
                setState { copy(selectedDate = next) }
                loadAppointments(next)
            }
            is CalendarEvent.PreviousDay -> {
                val prev = currentState.selectedDate.minusDays(1)
                setState { copy(selectedDate = prev) }
                loadAppointments(prev)
            }
            is CalendarEvent.AppointmentClicked -> {
                sendEffect(CalendarEffect.NavigateToAppointment(event.appointmentId))
            }
        }
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
