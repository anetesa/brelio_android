package com.brelio.feature.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.brelio.core.designsystem.R
import com.brelio.domain.model.Appointment
import com.brelio.domain.model.AppointmentStatus
import java.time.format.DateTimeFormatter

private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun AppointmentCard(
    appointment: Appointment,
    hourHeight: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val startMinutes = appointment.startAt.hour * 60 + appointment.startAt.minute
    val endMinutes = appointment.endAt.hour * 60 + appointment.endAt.minute
    val durationMinutes = (endMinutes - startMinutes).coerceAtLeast(30)

    val cardHeight = with(LocalDensity.current) {
        (hourHeight.toPx() * durationMinutes / 60f).toDp()
    }

    val backgroundColor = when (appointment.status) {
        AppointmentStatus.Confirmed -> colorResource(R.color.appointment_confirmed)
        AppointmentStatus.Pending -> colorResource(R.color.appointment_pending)
        AppointmentStatus.Cancelled -> colorResource(R.color.appointment_cancelled)
        AppointmentStatus.NoShow -> colorResource(R.color.appointment_no_show)
        AppointmentStatus.Completed -> colorResource(R.color.appointment_completed)
    }

    val shape = RoundedCornerShape(dimensionResource(R.dimen.radius_sm))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = cardHeight)
            .clip(shape)
            .background(backgroundColor.copy(alpha = 0.2f))
            .clickable(onClick = onClick)
            .padding(dimensionResource(R.dimen.spacing_sm)),
    ) {
        Text(
            text = appointment.clientName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        appointment.serviceName?.let { service ->
            Text(
                text = service,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = "${appointment.startAt.format(TIME_FORMATTER)} - ${appointment.endAt.format(TIME_FORMATTER)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
