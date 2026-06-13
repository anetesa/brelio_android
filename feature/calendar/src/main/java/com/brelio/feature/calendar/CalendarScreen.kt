package com.brelio.feature.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brelio.core.designsystem.R
import com.brelio.core.ui.LoadingScreen
import com.brelio.feature.calendar.components.AppointmentCard
import com.brelio.feature.calendar.components.DateNavigationBar
import com.brelio.feature.calendar.components.TimeSlot
import java.time.Duration

@Composable
fun CalendarScreen(
    onAppointmentClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        DateNavigationBar(
            selectedDate = state.selectedDate,
            onPreviousDay = { viewModel.onEvent(CalendarEvent.PreviousDay) },
            onNextDay = { viewModel.onEvent(CalendarEvent.NextDay) },
        )

        if (state.isLoading) {
            LoadingScreen()
            return@Column
        }

        DayView(
            state = state,
            onAppointmentClick = onAppointmentClick,
        )
    }
}

@Composable
private fun DayView(
    state: CalendarState,
    onAppointmentClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hourHeightDp = dimensionResource(R.dimen.calendar_hour_height)
    val gutterWidth = dimensionResource(R.dimen.calendar_time_gutter_width)
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            for (hour in 0..23) {
                TimeSlot(hour = hour)
            }
        }

        state.appointments.forEach { appointment ->
            val startMinutes = appointment.startAt.hour * 60 + appointment.startAt.minute
            val endMinutes = appointment.endAt.hour * 60 + appointment.endAt.minute
            val durationMinutes = (endMinutes - startMinutes).coerceAtLeast(30)

            val topOffset = with(LocalDensity.current) {
                (hourHeightDp.toPx() * startMinutes / 60f).toDp()
            }
            val cardHeight = with(LocalDensity.current) {
                (hourHeightDp.toPx() * durationMinutes / 60f).toDp()
            }

            AppointmentCard(
                appointment = appointment,
                hourHeight = hourHeightDp,
                onClick = { onAppointmentClick(appointment.id) },
                modifier = Modifier
                    .padding(
                        start = gutterWidth + dimensionResource(R.dimen.spacing_xs),
                        top = topOffset,
                        end = dimensionResource(R.dimen.spacing_sm),
                    ),
            )
        }
    }
}
